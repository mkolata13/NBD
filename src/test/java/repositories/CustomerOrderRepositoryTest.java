package repositories;

import model.*;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomerOrderRepositoryTest {

    private CustomerOrderRepository customerOrderRepository;
    private ProductRepository productRepository;
    private ClientRepository clientRepository;

    @BeforeAll
    public void setup() {
        customerOrderRepository = new CustomerOrderRepository();
        productRepository = new ProductRepository();
        clientRepository = new ClientRepository();
    }

    @BeforeEach
    public void cleanup() {
        customerOrderRepository.getAllOrders().forEach(customerOrder -> customerOrderRepository.deleteCustomerOrder(customerOrder.getEntityId()));
        productRepository.getAllProducts().forEach(product -> productRepository.deleteProduct(product.getEntityId()));
    }

    @Test
    public void testAddCustomerOrder() {
        Client client = new Client("Jane", "Doe", "123456789", new ClientTypeDefault());
        Product product = new Product("Product A", 15.0, 1.5, 5, "High-quality item A");

        productRepository.addProduct(product);

        customerOrderRepository.addCustomerOrder(client, product);

        List<CustomerOrder> orders = customerOrderRepository.getAllOrders();
        assertEquals(1, orders.size());
        assertEquals(1, orders.get(0).getProducts().size());
        assertEquals(product.getEntityId(), orders.get(0).getProducts().get(0).getEntityId());
    }

    @Test
    public void testAddCustomerOrderWithMultipleProducts() {
        Client client = new Client("Jane", "Doe", "123456789", new ClientTypeDefault());
        Product product1 = new Product("Product A", 15.0, 1.5, 10, "High-quality item A");
        Product product2 = new Product("Product B", 10.0, 1.5, 5, "High-quality item B");

        productRepository.addProduct(product1);
        productRepository.addProduct(product2);

        customerOrderRepository.addCustomerOrder(client, List.of(product1, product2));

        List<CustomerOrder> orders = customerOrderRepository.getAllOrders();
        assertEquals(1, orders.size());
        assertEquals(2, orders.get(0).getProducts().size());
        assertEquals(product1.getEntityId(), orders.get(0).getProducts().get(0).getEntityId());
        assertEquals(product2.getEntityId(), orders.get(0).getProducts().get(1).getEntityId());
    }

    @Test
    public void testOrderPriceWithAllClientTypes() {
        Client clientDefault = new Client("Janusz", "Kowalski", "123456789", new ClientTypeDefault());
        Client clientSilver = new Client("Jan", "Nowak", "123456789", new ClientTypeSilver());
        Client clientGold = new Client("Tomasz", "Kowalski", "123456789", new ClientTypeGold());

        Product product1 = new Product("Product A", 20.0, 1.5, 10, "High-quality item A");
        Product product2 = new Product("Product B", 10.0, 1.5, 10, "High-quality item B");

        productRepository.addProduct(product1);
        productRepository.addProduct(product2);

        customerOrderRepository.addCustomerOrder(clientDefault, List.of(product1, product2));
        customerOrderRepository.addCustomerOrder(clientSilver, List.of(product1, product2));
        customerOrderRepository.addCustomerOrder(clientGold, List.of(product1, product2));

        double baseProductsPrice = product1.getBasePrice() + product2.getBasePrice();

        assertEquals(customerOrderRepository.getAllClientOrders(clientDefault.getEntityId()).get(0).getOrderPrice(), baseProductsPrice * (1 - clientDefault.getClientType().getDiscount()));
        assertEquals(customerOrderRepository.getAllClientOrders(clientSilver.getEntityId()).get(0).getOrderPrice(), baseProductsPrice * (1 - clientSilver.getClientType().getDiscount()));
        assertEquals(customerOrderRepository.getAllClientOrders(clientGold.getEntityId()).get(0).getOrderPrice(), baseProductsPrice * (1 - clientGold.getClientType().getDiscount()));
    }

    @Test
    public void testClientIdEqualCustomerOrderClientId() {
        Client client = new Client("Janusz", "Kowalski", "123456789", new ClientTypeDefault());
        Product product = new Product("Product A", 15.0, 1.5, 10, "High-quality item A");

        clientRepository.addClient(client);
        productRepository.addProduct(product);
        customerOrderRepository.addCustomerOrder(client, product);

        List<CustomerOrder> orders = customerOrderRepository.getAllClientOrders(client.getEntityId());
        Client clientFromDB = clientRepository.getClientById(client.getEntityId());

        assertEquals(orders.get(0).getClient().getEntityId(), clientFromDB.getEntityId());
    }

    @Test
    public void testCustomerOrderWithNoProductAvailable() {
        Client client = new Client("Tomasz", "Kowalski", "123456789", new ClientTypeGold());
        Product product = new Product("Product A", 20.0, 1.5, 1, "High-quality item A");

        productRepository.addProduct(product);

        customerOrderRepository.addCustomerOrder(client, product);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customerOrderRepository.addCustomerOrder(client, product);
        });

        assertEquals("Transaction failed: Product " + product.getEntityId() + " is unavailable and cannot be ordered.",
                exception.getMessage());
    }

    @Test
    public void testCustomerOrderWithAvailableAndUnavailableProducts() {
        Client client = new Client("Tomasz", "Kowalski", "123456789", new ClientTypeGold());
        Product productAvailable = new Product("Product A", 20.0, 1.5, 1, "High-quality item A");
        Product productUnavailable = new Product("Product B", 10.0, 1.5, 0, "High-quality item B");

        productRepository.addProduct(productAvailable);
        productRepository.addProduct(productUnavailable);

        customerOrderRepository.addCustomerOrder(client, List.of(productAvailable, productUnavailable));

        List<CustomerOrder> orders = customerOrderRepository.getAllClientOrders(client.getEntityId());

        assertEquals(1, orders.get(0).getProducts().size());
        assertEquals(productAvailable.getEntityId(), orders.get(0).getProducts().get(0).getEntityId());
    }

    @Test
    public void testDeleteCustomerOrder() {
        Client client = new Client("Janusz", "Kowalski", "123456789", new ClientTypeDefault());
        Product product = new Product("Apple", 10, 2, 3, "abcdefgh");
        productRepository.addProduct(product);

        customerOrderRepository.addCustomerOrder(client, product);
        ObjectId orderId = customerOrderRepository.getAllClientOrders(client.getEntityId()).get(0).getEntityId();
        customerOrderRepository.deleteCustomerOrder(orderId);

        CustomerOrder foundOrder = customerOrderRepository.getById(orderId);

        assertNull(foundOrder);
        assertTrue(customerOrderRepository.getAllOrders().isEmpty());
    }

    @Test
    public void testEmptyProductListOrder() {
        Client client = new Client("Tomasz", "Kowalski", "123456789", new ClientTypeGold());
        List<Product> products = new ArrayList<>();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customerOrderRepository.addCustomerOrder(client, products);
        });

        assertEquals("Transaction failed: Order cannot be empty", exception.getMessage());
    }

    @Test
    public void testCustomerOrderWithUnavailableProductList() {
        Client client = new Client("Tomasz", "Kowalski", "123456789", new ClientTypeGold());
        Product product1 = new Product("Product A", 20.0, 1.5, 0, "High-quality item A");
        Product product2 = new Product("Product B", 10.0, 1.5, 0, "High-quality item B");

        productRepository.addProduct(product1);
        productRepository.addProduct(product2);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customerOrderRepository.addCustomerOrder(client, List.of(product1, product2));
        });

        assertEquals("Transaction failed: No products are available for ordering.", exception.getMessage());
    }
}