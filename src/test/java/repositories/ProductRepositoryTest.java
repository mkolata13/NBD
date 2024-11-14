package repositories;

import org.junit.jupiter.api.*;
import model.Product;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductRepositoryTest {

    private ProductRepository productRepository;

    @BeforeAll
    public void setup() {
        productRepository = new ProductRepository();
    }

    @BeforeEach
    @AfterAll
    public void cleanup() {
        productRepository.dropCollection();
    }

    @Test
    public void testAddProduct() {
        Product product = new Product("Apple", 10, 2, 3, "abcdefgh");
        productRepository.addProduct(product);

        List<Product> products = productRepository.getAllProducts();
        assertThat(products, hasSize(1));
        assertThat(products.get(0).getName(), equalTo("Apple"));
    }

    @Test
    public void testGetById() {
        Product product = new Product("Apple", 10, 2, 3, "abcdefgh");
        productRepository.addProduct(product);

        Product foundProduct = productRepository.getById(product.getEntityId());

        assertThat(foundProduct, notNullValue());
        assertThat(foundProduct.getEntityId(), equalTo(product.getEntityId()));
    }

    @Test
    public void testDeleteProduct() {
        Product product = new Product("Apple", 10, 2, 3, "abcdefgh");
        productRepository.addProduct(product);
        productRepository.deleteProduct(product.getEntityId());

        Product foundProduct = productRepository.getById(product.getEntityId());

        assertThat(foundProduct, nullValue());
        assertThat(productRepository.getAllProducts(), empty());
    }

    @Test
    public void testDecrementQuantityOfProduct() {
        Product product = new Product("Apple", 10, 2, 5, "abcdefgh");
        productRepository.addProduct(product);

        productRepository.decrementQuantityOfProduct(product.getEntityId());

        Product updatedProduct = productRepository.getById(product.getEntityId());

        assertThat(updatedProduct.getQuantity(), equalTo(4));
    }

    @Test
    public void testSetProductUnAvailability() {
        Product product = new Product("Apple", 10, 2, 3, "abcdefgh");
        productRepository.addProduct(product);

        productRepository.setProductUnAvailability(product.getEntityId());

        Product updatedProduct = productRepository.getById(product.getEntityId());

        assertThat(updatedProduct.isAvailable(), is(false));
    }
}
