package managers;

import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import model.Product;
import repositories.ProductRepository;

import java.util.List;
import java.util.Set;

public class ProductManager {

    private final ProductRepository productRepository;
    private final Validator validator;

    public ProductManager(EntityManager em, Validator validator) {
        this.productRepository = new ProductRepository(em);
        this.validator = validator;
    }

    public Product getProduct(Long id){
        return productRepository.findById(id);
    }

    public void addProduct(String name, double basePrice, double weight, int quantity, String description) {
        Product product = new Product(name, basePrice, weight, quantity, description);
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        if(!violations.isEmpty()) {
            for(ConstraintViolation<Product> violation : violations) {
                throw new RuntimeException(violation.getMessage());
            }
        }
        productRepository.addProduct(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.getAll();
    }
}
