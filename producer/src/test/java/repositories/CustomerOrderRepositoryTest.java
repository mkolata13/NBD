package repositories;

import model.*;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import repositories.mongodb.ClientRepositoryMongo;
import repositories.mongodb.CustomerOrderRepositoryMongo;
import repositories.mongodb.ProductRepositoryMongo;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomerOrderRepositoryTest {

    private CustomerOrderRepository customerOrderRepository;
    private ProductRepository productRepository;
    private ClientRepository clientRepository;

    @BeforeAll
    public void setup() {
        customerOrderRepository = new CustomerOrderRepositoryMongo();
        productRepository = new ProductRepositoryMongo();
        clientRepository = new ClientRepositoryMongo();
    }

    @BeforeEach
    public void cleanup() {
        customerOrderRepository.dropCollection();
        productRepository.dropCollection();
        clientRepository.dropCollection();
    }

    @Test
    public void testAddCustomerOrder() {
        Client client = new Client("Jane", "Doe", "123456789", new ClientTypeDefault());
        Product product = new Product("Product A", 15.0, 1.5, 5, "High-quality item A");

        productRepository.addProduct(product);
        clientRepository.addClient(client);

        customerOrderRepository.addCustomerOrder(client, product);

        List<CustomerOrder> orders = customerOrderRepository.getAllOrders();
        assertThat(orders, hasSize(1));
        assertThat(orders.get(0).getProducts(), hasSize(1));
        assertThat(orders.get(0).getProducts(), hasItem(hasProperty("entityId", is(product.getEntityId()))));
    }

    @Test
    public void testAddCustomerOrderWithMultipleProducts() {
        Client client = new Client("Jane", "Doe", "123456789", new ClientTypeDefault());
        Product product1 = new Product("Product A", 15.0, 1.5, 10, "High-quality item A");
        Product product2 = new Product("Product B", 10.0, 1.5, 5, "High-quality item B");

        productRepository.addProduct(product1);
        productRepository.addProduct(product2);

        clientRepository.addClient(client);

        customerOrderRepository.addCustomerOrder(client, List.of(product1, product2));

        List<CustomerOrder> orders = customerOrderRepository.getAllOrders();
        assertThat(orders, hasSize(1));
        assertThat(orders.get(0).getProducts(), hasSize(2));
        assertThat(orders.get(0).getProducts(), hasItem(hasProperty("entityId", is(product1.getEntityId()))));
        assertThat(orders.get(0).getProducts(), hasItem(hasProperty("entityId", is(product2.getEntityId()))));
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

        assertThat(customerOrderRepository.getAllClientOrders(clientDefault.getEntityId()).get(0).getOrderPrice(),
                closeTo(baseProductsPrice * (1 - clientDefault.getClientType().getDiscount()), 0.01));
        assertThat(customerOrderRepository.getAllClientOrders(clientSilver.getEntityId()).get(0).getOrderPrice(),
                closeTo(baseProductsPrice * (1 - clientSilver.getClientType().getDiscount()), 0.01));
        assertThat(customerOrderRepository.getAllClientOrders(clientGold.getEntityId()).get(0).getOrderPrice(),
                closeTo(baseProductsPrice * (1 - clientGold.getClientType().getDiscount()), 0.01));
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

        assertThat(orders.get(0).getClient().getEntityId(), equalTo(clientFromDB.getEntityId()));
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

        assertThat(exception.getMessage(), is("Transaction failed: Product " + product.getEntityId()
                + " is unavailable and cannot be ordered."));
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

        assertThat(orders.get(0).getProducts(), hasSize(1));
        assertThat(orders.get(0).getProducts().get(0).getEntityId(), equalTo(productAvailable.getEntityId()));
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
        assertThat(customerOrderRepository.getAllOrders(), empty());
    }

    @Test
    public void testEmptyProductListOrder() {
        Client client = new Client("Tomasz", "Kowalski", "123456789", new ClientTypeGold());
        List<Product> products = new ArrayList<>();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customerOrderRepository.addCustomerOrder(client, products);
        });

        assertThat(exception.getMessage(), is("Transaction failed: Order cannot be empty"));
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

        assertThat(exception.getMessage(), is("Transaction failed: No products are available for ordering."));
    }
}