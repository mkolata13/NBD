package repositories;

import org.junit.jupiter.api.*;
import model.Product;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductRepositoryTest {

    private ProductRepository productRepository;

    @BeforeAll
    public void setup() {
        productRepository = new ProductRepository();
    }

    @BeforeEach
    public void cleanup() {
        productRepository.getAllProducts().forEach(product -> productRepository.deleteProduct(product.getEntityId()));
    }

    @Test
    public void testAddProduct() {
        Product product = new Product("Apple", 10, 2, 3, "abcdefgh");
        productRepository.addProduct(product);

        List<Product> products = productRepository.getAllProducts();
        assertEquals(1, products.size());
        assertEquals("Apple", products.get(0).getName());
    }

    @Test
    public void testGetById() {
        Product product = new Product("Apple", 10, 2, 3, "abcdefgh");
        productRepository.addProduct(product);

        Product foundProduct = productRepository.getById(product.getEntityId());

        assertNotNull(foundProduct);
        assertEquals(product.getEntityId(), foundProduct.getEntityId());
    }

    @Test
    public void testDeleteProduct() {
        Product product = new Product("Apple", 10, 2, 3, "abcdefgh");
        productRepository.addProduct(product);
        productRepository.deleteProduct(product.getEntityId());

        Product foundProduct = productRepository.getById(product.getEntityId());

        assertNull(foundProduct);
        assertTrue(productRepository.getAllProducts().isEmpty());
    }

    @Test
    public void testDecrementQuantityOfProduct() {
        Product product = new Product("Apple", 10, 2, 5, "abcdefgh");
        productRepository.addProduct(product);

        productRepository.decrementQuantityOfProduct(product.getEntityId());

        Product updatedProduct = productRepository.getById(product.getEntityId());

        assertEquals(4, updatedProduct.getQuantity());
    }

    @Test
    public void testSetProductUnAvailability() {
        Product product = new Product("Apple", 10, 2, 3, "abcdefgh");
        productRepository.addProduct(product);

        productRepository.setProductUnAvailability(product.getEntityId());

        Product updatedProduct = productRepository.getById(product.getEntityId());
        assertFalse(updatedProduct.isAvailable());
    }
}
