package repositories;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import model.CustomerOrder;

public class CustomerOrderRepository extends AbstractMongoRepository {
    private final MongoCollection<CustomerOrder> customerOrdersCollection;

    public CustomerOrderRepository() {
        customerOrdersCollection = getMongoDatabase().getCollection("customerorders", CustomerOrder.class);
    }

    public void addCustomerOrder(CustomerOrder customerOrder) {
        try (ClientSession session = getClientSession()) {
            session.startTransaction();
            customerOrdersCollection.insertOne(customerOrder);
            session.commitTransaction();
        } catch (Exception e) {
            throw new RuntimeException("Transaction failed: " + e.getMessage(), e);
        }
    }

    public void dropCollection() {
        customerOrdersCollection.drop();
    }
}
