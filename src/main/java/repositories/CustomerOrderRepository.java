package repositories;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import model.Client;
import model.CustomerOrder;
import model.Product;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class CustomerOrderRepository extends AbstractMongoRepository {
    private final MongoCollection<CustomerOrder> customerOrdersCollection;
    private final ProductRepository productRepository;

    public CustomerOrderRepository() {
        customerOrdersCollection = getMongoDatabase().getCollection("customerorders", CustomerOrder.class);
        productRepository = new ProductRepository();
    }

    public void addCustomerOrder(Client client, List<Product> products) {
        try (ClientSession session = getClientSession()) {
            session.startTransaction();

            if (products.isEmpty()) {
                session.abortTransaction();
                throw new RuntimeException("Order cannot be empty");
            }

            List<Product> availableProducts = new ArrayList<>();

            for (Product product : products) {
                Product lockedProduct = productRepository.getById(product.getEntityId());
                if (lockedProduct != null && lockedProduct.isAvailable()) {
                    productRepository.decrementQuantityOfProduct(lockedProduct.getEntityId());
                    if (lockedProduct.getQuantity() == 1) {
                        productRepository.setProductUnAvailability(lockedProduct.getEntityId());
                    }
                    availableProducts.add(lockedProduct);
                } else {
                    System.out.println("Product " + product.getEntityId() + " is unavailable and will not be ordered.");
                }
            }

            if (availableProducts.isEmpty()) {
                session.abortTransaction();
                throw new RuntimeException("No products are available for ordering.");
            }

            CustomerOrder customerOrder = new CustomerOrder(client, availableProducts);
            customerOrdersCollection.insertOne(customerOrder);
            session.commitTransaction();
        } catch (Exception e) {
            throw new RuntimeException("Transaction failed: " + e.getMessage(), e);
        }
    }

    public void addCustomerOrder(Client client, Product product) {
        try (ClientSession session = getClientSession()) {
            session.startTransaction();

            Product lockedProduct = productRepository.getById(product.getEntityId());
            if (lockedProduct == null || !lockedProduct.isAvailable()) {
                session.abortTransaction();
                throw new RuntimeException("Product " + product.getEntityId()
                                            + " is unavailable and cannot be ordered.");
            } else {
                productRepository.decrementQuantityOfProduct(lockedProduct.getEntityId());
                if (lockedProduct.getQuantity() == 1) {
                    productRepository.setProductUnAvailability(lockedProduct.getEntityId());
                }
            }

            CustomerOrder customerOrder = new CustomerOrder(client, lockedProduct);
            customerOrdersCollection.insertOne(customerOrder);
            session.commitTransaction();
        } catch (Exception e) {
            throw new RuntimeException("Transaction failed: " + e.getMessage(), e);
        }
    }

    public void deleteCustomerOrder(ObjectId id) {
        customerOrdersCollection.findOneAndDelete(Filters.eq("_id", id));
    }

    public CustomerOrder getById(ObjectId id) {
        return customerOrdersCollection.find(Filters.eq("_id", id)).first();
    }

    public List<CustomerOrder> getAllClientOrders(ObjectId id) {
        return customerOrdersCollection.find(Filters.eq("client._id", id)).into(new ArrayList<>());
    }

    public List<CustomerOrder> getAllOrders() {
        return customerOrdersCollection.find().into(new ArrayList<>());
    }
}
