import model.*;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import repositories.CustomerOrderRepository;
import repositories.mongodb.CustomerOrderRepositoryMongo;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProducerTest {
    private static CustomerOrder customerOrder1;
    private static CustomerOrder customerOrder2;
    private static CustomerOrder customerOrder3;

    private static CustomerOrderRepository customerOrderRepository;

    @BeforeAll
    public void setUp() {
        customerOrderRepository = new CustomerOrderRepositoryMongo();

        Client client1 = new Client("John", "Doe", "123456789", new ClientTypeGold());
        Client client2 = new Client("Jane", "Doe", "987654321", new ClientTypeSilver());
        Client client3 = new Client("John", "Smith", "123456789", new ClientTypeDefault());
        Product product1 = new Product("product1", 10, 1, 5, "product1 description");
        Product product2 = new Product("product2", 20, 2, 5, "product2 description");
        Product product3 = new Product("product3", 30, 3, 5, "product3 description");

        customerOrder1 = new CustomerOrder(client1, List.of(product1, product2));
        customerOrder2 = new CustomerOrder(client2, List.of(product2, product3));
        customerOrder3 = new CustomerOrder(client3, List.of(product1, product3));
    }

    @Test
    public void sendCustomerOrders() {
        customerOrderRepository.addCustomerOrder(customerOrder1);
        customerOrderRepository.addCustomerOrder(customerOrder2);
        customerOrderRepository.addCustomerOrder(customerOrder3);
    }
}
