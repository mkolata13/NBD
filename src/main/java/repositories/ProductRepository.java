package repositories;

import model.Product;
import org.bson.types.ObjectId;

import java.util.List;

public interface ProductRepository {

    void addProduct(Product product);

    void deleteProduct(ObjectId id);

    void decrementQuantityOfProduct(ObjectId id);

    void setProductUnAvailability(ObjectId id);

    Product getById(ObjectId id);

    List<Product> getAllProducts();

    void dropCollection();

    void closeCache();
}
