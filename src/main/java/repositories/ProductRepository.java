package repositories;

import model.Product;

import java.util.UUID;

public interface ProductRepository {

    boolean addProduct(Product product);

    boolean deleteProduct(Product product);

    void updateProduct(Product product);

    Product getById(UUID id);
}
