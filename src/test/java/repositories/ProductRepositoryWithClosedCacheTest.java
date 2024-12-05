package repositories;

import org.junit.jupiter.api.*;
import model.Product;
import repositories.mongodb.ProductRepositoryMongo;
import repositories.redis.ProductRepositoryCacheDecorator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductRepositoryWithClosedCacheTest {

    private ProductRepository productRepository;

    @BeforeAll
    public void setup() {
        productRepository = new ProductRepositoryCacheDecorator(new ProductRepositoryMongo());
    }

    @Test
    public void testGetById() {
        Product product = new Product("Apple", 10, 2, 3, "abcdefgh");
        productRepository.addProduct(product);

        productRepository.closeCache();
        Product foundProduct = productRepository.getById(product.getEntityId());

        assertThat(foundProduct, notNullValue());
        assertThat(foundProduct, is(equalTo(product)));
    }
}