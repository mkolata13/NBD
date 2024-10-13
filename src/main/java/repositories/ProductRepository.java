package repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import model.Client;
import model.Product;

import java.util.List;

public class ProductRepository {

    private final EntityManager em;

    public ProductRepository(EntityManager em) {
        this.em = em;
    }

    public void addProduct(Product product) {
        try {
            em.getTransaction().begin();
            if (product.getId() == null) {
                em.persist(product);
            } else {
                product = em.merge(product);
            }
            em.getTransaction().commit();
            em.detach(product);
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    public void removeProduct(Long id) {
        try {
            em.getTransaction().begin();
            Product product = findById(id);
            em.remove(product);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    public void decrementQuantityOfProduct(Long id) {
        Product product = em.find(Product.class, id);
        if (product != null) {
            int i = product.getQuantity();
            if (i > 0) {
                product.setQuantity(i - 1);
                em.merge(product);
            } else {
                throw new RuntimeException("Product is unavailable.");
            }
        }
    }

    public void setProductUnAvailability(Long id) {
        Product product = em.find(Product.class, id);
        if (product != null) {
            product.setAvailable(false);
            em.merge(product);
        } else {
            throw new NoResultException("No such product.");
        }
    }

    public Product findById(Long id) {
        return em.find(Product.class, id);
    }

    public List<Product> getAll() {
        TypedQuery<Product> query = em.createQuery("Select p from Product p", Product.class);
        return query.getResultList();
    }
}
