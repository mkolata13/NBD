package repositories.redis;

import model.Product;
import org.bson.types.ObjectId;
import redis.ProductCache;
import redis.clients.jedis.exceptions.JedisException;
import repositories.ProductRepository;

import java.util.List;

public class ProductRepositoryCacheDecorator implements ProductRepository {

    private final ProductRepository wrappedRepository;
    private final ProductCache productCache;

    public ProductRepositoryCacheDecorator(ProductRepository repository) {
        this.wrappedRepository = repository;
        this.productCache = new ProductCache();
    }

    @Override
    public void addProduct(Product product) {
        wrappedRepository.addProduct(product);
        productCache.addProduct(product);
    }

    @Override
    public void deleteProduct(ObjectId id) {
        wrappedRepository.deleteProduct(id);
        productCache.deleteProduct(id);
    }

    @Override
    public void decrementQuantityOfProduct(ObjectId id) {
        try {
            productCache.decrementQuantityOfProduct(id);
        } catch (RuntimeException e) {
            System.out.println("Cache update failed; delegating to MongoDB.");
        }
        wrappedRepository.decrementQuantityOfProduct(id);
    }

    @Override
    public void setProductUnAvailability(ObjectId id) {
        try {
            productCache.setProductUnAvailability(id);
        } catch (RuntimeException e) {
            System.out.println("Cache update failed; delegating to MongoDB.");
        }
        wrappedRepository.setProductUnAvailability(id);
    }

    @Override
    public Product getById(ObjectId id) {
        try {
            return productCache.getProduct(id);
        } catch (JedisException e) {
            return wrappedRepository.getById(id);
        }
    }

    @Override
    public List<Product> getAllProducts() {
        try {
            return productCache.getAllProducts();
        } catch (JedisException e) {
            return wrappedRepository.getAllProducts();
        }
    }

    @Override
    public void dropCollection() {
        wrappedRepository.dropCollection();
        productCache.clearCache();
    }

    @Override
    public void closeCache() {
        productCache.closeCache();
    }
}
