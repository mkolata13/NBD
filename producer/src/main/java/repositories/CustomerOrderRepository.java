package repositories;

import model.Client;
import model.CustomerOrder;
import model.Product;
import org.bson.types.ObjectId;

import java.util.List;

public interface CustomerOrderRepository {

    void addCustomerOrder(Client client, List<Product> products);

    void addCustomerOrder(Client client, Product product);

    void addCustomerOrder(CustomerOrder customerOrder);

    void deleteCustomerOrder(ObjectId id);

    CustomerOrder getById(ObjectId id);

    List<CustomerOrder> getAllClientOrders(ObjectId id);

    List<CustomerOrder> getAllOrders();

    void dropCollection();
}
