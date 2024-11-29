package repositories;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import model.Product;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import redis.ProductCache;
import redis.clients.jedis.exceptions.JedisException;

import java.util.ArrayList;
import java.util.List;

public class ProductRepository extends AbstractMongoRepository {

    private final MongoCollection<Product> productsCollection;
    private final ProductCache productCache;

    public ProductRepository() {
        this.productsCollection = getMongoDatabase().getCollection("products", Product.class);
        this.productCache = new ProductCache();
    }

    public void addProduct(Product product) {
        productsCollection.insertOne(product);
        productCache.addProduct(product);
    }

    public void deleteProduct(ObjectId id) {
        productsCollection.findOneAndDelete(Filters.eq("_id", id));
        productCache.deleteProduct(id);
    }

    public void decrementQuantityOfProduct(ObjectId id) {
        productCache.decrementQuantityOfProduct(id);

        Bson filter = Filters.and(Filters.eq("_id", id), Filters.gt("quantity", 0));
        Bson update = Updates.inc("quantity", -1);

        UpdateResult result = productsCollection.updateOne(filter, update);
        if (result.getModifiedCount() == 0) {
            System.out.println("Product quantity could not be decremented. It may be out of stock.");
        }
    }

    public void setProductUnAvailability(ObjectId id) {
        productCache.setProductUnAvailability(id);

        Bson filter = Filters.eq("_id", id);
        Bson update = Updates.set("available", false);

        UpdateResult result = productsCollection.updateOne(filter, update);
        if (result.getModifiedCount() == 0) {
            System.out.println("Product quantity could not be decremented. It may be out of stock.");
        }
    }

    public Product getById(ObjectId id) {
        try {
            return productCache.getProduct(id);
        } catch (JedisException e) {
            return productsCollection.find(Filters.eq("_id", id)).first();
        }
    }

    public List<Product> getAllProducts() {
        try {
            return productCache.getAllProducts();
        } catch (JedisException e) {
            return productsCollection.find().into(new ArrayList<>());
        }
    }

    public void dropCollection() {
        productsCollection.drop();
        productCache.clearCache();
    }

    public void closeCache() {
        productCache.clearCache();
        productCache.closeCache();
    }
}
