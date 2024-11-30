package repositories.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import model.Product;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import repositories.AbstractMongoRepository;
import repositories.ProductRepository;

import java.util.ArrayList;
import java.util.List;

public class ProductRepositoryMongo extends AbstractMongoRepository implements ProductRepository {

    private final MongoCollection<Product> productsCollection;

    public ProductRepositoryMongo() {
        this.productsCollection = getMongoDatabase().getCollection("products", Product.class);
    }

    public void addProduct(Product product) {
        productsCollection.insertOne(product);
    }

    public void deleteProduct(ObjectId id) {
        productsCollection.findOneAndDelete(Filters.eq("_id", id));
    }

    public void decrementQuantityOfProduct(ObjectId id) {
        Bson filter = Filters.and(Filters.eq("_id", id), Filters.gt("quantity", 0));
        Bson update = Updates.inc("quantity", -1);

        UpdateResult result = productsCollection.updateOne(filter, update);
        if (result.getModifiedCount() == 0) {
            System.out.println("Product quantity could not be decremented. It may be out of stock.");
        }
    }

    public void setProductUnAvailability(ObjectId id) {
        Bson filter = Filters.eq("_id", id);
        Bson update = Updates.set("available", false);

        UpdateResult result = productsCollection.updateOne(filter, update);
        if (result.getModifiedCount() == 0) {
            System.out.println("Product quantity could not be decremented. It may be out of stock.");
        }
    }

    public Product getById(ObjectId id) {
        return productsCollection.find(Filters.eq("_id", id)).first();
    }

    public List<Product> getAllProducts() {
        return productsCollection.find().into(new ArrayList<>());
    }

    public void dropCollection() {
        productsCollection.drop();
    }

    public void closeCache() {
    }
}
