package repositories;

import org.junit.jupiter.api.*;
import model.Product;
import repositories.cassandra.CassandraProductRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductRepositoryTest {

    private ProductRepository productRepository;

    @BeforeAll
    public void setup() {
        productRepository = new CassandraProductRepository();
    }

    @Test
    public void testAddProduct() {
        Product product = new Product("Apple", 10, 2, 3, "abcdefgh");
        assertThat(productRepository.addProduct(product), equalTo(true));
    }

    @Test
    public void testGetById() {
        Product product = new Product("Apple", 10, 2, 3, "abcdefgh");
        productRepository.addProduct(product);

        Product foundProduct = productRepository.getById(product.getProductId());

        assertThat(foundProduct, notNullValue());
        assertThat(foundProduct, is(equalTo(product)));
    }

    @Test
    public void testDeleteProduct() {
        Product product = new Product("Apple", 10, 2, 3, "abcdefgh");
        productRepository.addProduct(product);

        assertThat(productRepository.deleteProduct(product), equalTo(true));
    }

    @Test
    public void testUpdateProduct() {
        Product product = new Product("Apple", 10, 2, 3, "abcdefgh");
        productRepository.addProduct(product);

        product.setName("Banana");
        productRepository.updateProduct(product);

        assertThat(productRepository.getById(product.getProductId()), equalTo(product));
    }
}
