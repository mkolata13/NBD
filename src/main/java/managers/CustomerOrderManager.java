package managers;

import model.Client;
import model.CustomerOrder;
import model.Product;
import org.bson.types.ObjectId;
import repositories.CustomerOrderRepository;
import repositories.ProductRepository;

import java.util.List;

public class CustomerOrderManager {
    private final CustomerOrderRepository customerOrderRepository;
    private final ProductRepository productRepository;

    public CustomerOrderManager() {
        customerOrderRepository = new CustomerOrderRepository();
        productRepository = new ProductRepository();
    }

    public CustomerOrder findCustomerOrderById(ObjectId id) {
        return customerOrderRepository.getById(id);
    }

    public void addCustomerOrder(Client client, List<Product> products) {
        customerOrderRepository.addCustomerOrder(client, products);
    }

    public void addCustomerOrder(Client client, Product product) {
        customerOrderRepository.addCustomerOrder(client, product);
    }

    public void deleteCustomerOrder(ObjectId id) {
        customerOrderRepository.deleteCustomerOrder(id);
    }

    public List<CustomerOrder> getAllOrders() {
        return customerOrderRepository.getAllOrders();
    }

    public List<CustomerOrder> getAllClientOrders(ObjectId clientId) {
        return customerOrderRepository.getAllClientOrders(clientId);
    }
}
