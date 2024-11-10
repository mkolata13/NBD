package managers;

import model.Product;
import org.bson.types.ObjectId;
import repositories.ProductRepository;

import java.util.List;


public class ProductManager {

    private final ProductRepository productRepository;

    public ProductManager() {
        this.productRepository = new ProductRepository();
    }

    public Product getProduct(ObjectId id) {
        return productRepository.getById(id);
    }

    public void addProduct(String name, double basePrice, double weight, int quantity, String description) {
        Product product = new Product(name, basePrice, weight, quantity, description);
        productRepository.addProduct(product);
    }

    public void deleteProduct(ObjectId id) {
        productRepository.deleteProduct(id);
    }

    public List<Product> getAllProducts() {
        return productRepository.getAllProducts();
    }
}
