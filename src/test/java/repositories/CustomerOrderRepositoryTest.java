package repositories;

import model.Client;
import model.Product;
import model.CustomerOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repositories.cassandra.CassandraClientRepository;
import repositories.cassandra.CassandraCustomerOrderRepository;
import repositories.cassandra.CassandraProductRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomerOrderRepositoryTest {

    private CustomerOrderRepository customerOrderRepository;
    private ProductRepository productRepository;
    private ClientRepository clientRepository;

    @BeforeEach
    public void setup() {
        customerOrderRepository = new CassandraCustomerOrderRepository();
        productRepository = new CassandraProductRepository();
        clientRepository = new CassandraClientRepository();
    }

    @Test
    public void testAddCustomerOrder_SingleProduct() {
        Client client = new Client("John", "Doe", "123456789", "gold");
        Product product = new Product("Test Product", 50.0, 2.0, 5, "Sample description");

        clientRepository.addClient(client);
        productRepository.addProduct(product);

        boolean result = customerOrderRepository.addCustomerOrder(client, product);

        assertThat(result, equalTo(true));

        List<CustomerOrder> clientOrders = customerOrderRepository.getAllClientOrders(client.getClientId());
        System.out.println(clientOrders.get(0).toString());
        assertThat(clientOrders, hasItem(
                allOf(
                        hasProperty("clientId", equalTo(client.getClientId())),
                        hasProperty("productId", equalTo(product.getProductId()))
                )
        ));

        List<CustomerOrder> productOrders = customerOrderRepository.getAllProductOrders(product.getProductId());
        assertThat(productOrders, hasItem(
                allOf(
                        hasProperty("clientId", equalTo(client.getClientId())),
                        hasProperty("productId", equalTo(product.getProductId()))
                )
        ));
    }

    @Test
    public void testAddCustomerOrder_MultipleProducts() {
        Client client = new Client("Jane", "Doe", "987654321", "default");
        Product product1 = new Product("Product 1", 110.0, 1.0, 10, "Description 1");
        Product product2 = new Product("Product 2", 110.0, 1.5, 8, "Description 2");

        clientRepository.addClient(client);
        productRepository.addProduct(product1);
        productRepository.addProduct(product2);

        boolean result = customerOrderRepository.addCustomerOrder(client, List.of(product1, product2));
        assertThat(result, equalTo(true));

        List<CustomerOrder> clientOrders = customerOrderRepository.getAllClientOrders(client.getClientId());

        assertThat(clientOrders, hasSize(2)); // Dwa produkty powinny być w zamówieniu

        assertThat(clientOrders, hasItem(hasProperty("productId", equalTo(product1.getProductId()))));
        assertThat(clientOrders, hasItem(hasProperty("productId", equalTo(product2.getProductId()))));

        List<CustomerOrder> product1Orders = customerOrderRepository.getAllProductOrders(product1.getProductId());
        assertThat(product1Orders, hasItem(hasProperty("clientId", equalTo(client.getClientId()))));

        List<CustomerOrder> product2Orders = customerOrderRepository.getAllProductOrders(product2.getProductId());
        assertThat(product2Orders, hasItem(hasProperty("clientId", equalTo(client.getClientId()))));
    }

    @Test
    public void testGetAllClientOrders() {
        Client client = new Client("Mark", "Smith", "654654654", "default");
        Product product = new Product("Unique Product", 20.0, 2.0, 1, "Unique description");

        clientRepository.addClient(client);
        productRepository.addProduct(product);

        customerOrderRepository.addCustomerOrder(client, product);

        List<CustomerOrder> clientOrders = customerOrderRepository.getAllClientOrders(client.getClientId());

        assertThat(clientOrders, hasItem(
                allOf(
                        hasProperty("clientId", equalTo(client.getClientId())),
                        hasProperty("productId", equalTo(product.getProductId()))
                )
        ));
    }

    @Test
    public void testGetAllProductOrders() {
        Client client = new Client("Alice", "Brown", "789789789", "gold");
        Product product = new Product("Special Product", 60.0, 3.0, 5, "Special description");
        clientRepository.addClient(client);
        productRepository.addProduct(product);

        customerOrderRepository.addCustomerOrder(client, product);

        List<CustomerOrder> productOrders = customerOrderRepository.getAllProductOrders(product.getProductId());

        assertThat(productOrders, hasItem(
                allOf(
                        hasProperty("clientId", equalTo(client.getClientId())),
                        hasProperty("productId", equalTo(product.getProductId()))
                )
        ));
    }

    @Test
    public void testAddCustomerOrder_EmptyOrder() {
        Client client = new Client("Mark", "Smith", "654654654", "default");
        clientRepository.addClient(client);

        Exception exception = assertThrows(RuntimeException.class, () -> customerOrderRepository.addCustomerOrder(client, List.of()));

        assertThat(exception.getMessage(), containsString("Products in customer order cannot be empty."));
    }

    @Test
    public void testAddCustomerOrder_ProductUnavailable() {
        Client client = new Client("Alice", "Brown", "789789789", "gold");
        Product product = new Product("Unavailable Product", 60.0, 3.0, 0, "Unavailable description");
        clientRepository.addClient(client);
        productRepository.addProduct(product);

        boolean result = customerOrderRepository.addCustomerOrder(client, List.of(product));

        assertThat(result, equalTo(false));
    }

    @Test
    public void testAddCustomerOrder_ProductQuantityDecrement() {
        Client client = new Client("Bob", "White", "123123123", "silver");
        Product product = new Product("Decrement Product", 70.0, 2.5, 1, "Decrement description");
        clientRepository.addClient(client);
        productRepository.addProduct(product);

        boolean result = customerOrderRepository.addCustomerOrder(client, List.of(product));

        assertThat(result, equalTo(true));

        Product updatedProduct = productRepository.getById(product.getProductId());
        assertThat(updatedProduct.getQuantity(), equalTo(0));
        assertThat(updatedProduct.isAvailable(), equalTo(false));
    }
}
