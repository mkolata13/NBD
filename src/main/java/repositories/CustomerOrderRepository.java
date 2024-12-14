package repositories;

import model.Client;
import model.CustomerOrder;
import model.Product;

import java.util.List;
import java.util.UUID;

public interface CustomerOrderRepository {

    boolean addCustomerOrder(Client client, List<Product> products);

    boolean addCustomerOrder(Client client, Product product);

    List<CustomerOrder> getAllClientOrders(UUID id);

    List<CustomerOrder> getAllProductOrders(UUID id);
}
