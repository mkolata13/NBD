package redis;

import model.Product;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import redis.clients.jedis.JedisPooled;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductCacheTest {

    private ProductCache productCache;
    private JedisPooled pool;

    @BeforeAll
    void setup() {
        productCache = new ProductCache();
        pool = new RedisConnector().getJedisPool();
    }

    @BeforeEach
    @AfterAll
    void cleanup() {
        productCache.clearCache();
    }

    @Test
    void testAddProduct() {
        Product product = new Product("Test Product", 10.99, 1.2, 10, "Test Description");
        productCache.addProduct(product);

        String key = ("product:" + product.getEntityId());
        Object product2 = pool.jsonGet(key);

        assertNotNull(product2, "Product should be added to the cache");
    }

    @Test
    void testGetProduct() {
        Product product = new Product("Test Product", 10.99, 1.2, 10, "Test Description");
        productCache.addProduct(product);

        Product productFromCache = productCache.getProduct(product.getEntityId());

        assertThat(productFromCache, is(product));
    }

    @Test
    void testDeleteProduct() {
        Product product = new Product("Test Product", 10.99, 1.2, 10, "Test Description");
        productCache.addProduct(product);

        productCache.deleteProduct(product.getEntityId());
        assertNull(productCache.getProduct(product.getEntityId()));
    }

    @Test
    void testDecrementQuantityOfProduct() {
        Product product = new Product("Test Product", 10.99, 1.2, 10, "Test Description");
        productCache.addProduct(product);

        productCache.decrementQuantityOfProduct(product.getEntityId());

        Product updatedProduct = productCache.getProduct(product.getEntityId());
        assertThat(updatedProduct.getQuantity(), is(equalTo(9)));
    }

    @Test
    void testDecrementQuantityOfNonExistentProduct() {
        ObjectId nonExistentId = new ObjectId();
        Exception exception = assertThrows(RuntimeException.class, () -> productCache.decrementQuantityOfProduct(nonExistentId));

        assertThat(exception.getMessage(), containsString("Error while decrementing product."));
    }

    @Test
    void testSetProductUnAvailability() {
        Product product = new Product("Test Product", 10.99, 1.2, 10, "Test Description");
        productCache.addProduct(product);

        productCache.setProductUnAvailability(product.getEntityId());

        Product updatedProduct = productCache.getProduct(product.getEntityId());
        assertFalse(updatedProduct.isAvailable(), "Product should be marked as unavailable");
    }

    @Test
    void testSetUnAvailabilityOfNonExistentProduct() {
        ObjectId nonExistentId = new ObjectId();
        Exception exception = assertThrows(RuntimeException.class, () -> productCache.setProductUnAvailability(nonExistentId));

        assertThat(exception.getMessage(), containsString("Error while setting unavailability of product."));
    }

    @Test
    void testGetAllProducts() {
        Product product1 = new Product("Product 1", 5.99, 0.5, 20, "Description 1");
        Product product2 = new Product("Product 2", 15.99, 1.5, 15, "Description 2");

        productCache.addProduct(product1);
        productCache.addProduct(product2);

        List<Product> allProducts = productCache.getAllProducts();

        assertNotNull(allProducts, "List of products should not be null");
        assertThat(allProducts.size(), is(equalTo(2)));
        assertThat(allProducts, containsInAnyOrder(product1, product2));
    }

    @Test
    void testGetAllProductsWhenCacheIsEmpty() {
        List<Product> allProducts = productCache.getAllProducts();
        assertThat(allProducts, is(empty()));
    }
}