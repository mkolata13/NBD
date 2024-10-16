package managers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import model.Client;
import model.CustomerOrder;
import model.Product;
import repositories.CustomerOrderRepository;
import repositories.ProductRepository;

import java.util.ArrayList;
import java.util.List;

public class CustomerOrderManager {
    private final CustomerOrderRepository customerOrderRepository;
    private final ProductRepository productRepository;
    private EntityManager em;

    public CustomerOrderManager(EntityManager em) {
        this.customerOrderRepository = new CustomerOrderRepository(em);
        this.productRepository = new ProductRepository(em);
        this.em = em;
    }

    public CustomerOrder findCustomerOrderById(Long id) {
        return customerOrderRepository.findById(id);
    }

    public void addCustomerOrder(Client client, List<Product> products) {
        try {
            em.getTransaction().begin();
            if (products.isEmpty()) {
                em.getTransaction().rollback();
                throw new RuntimeException("Order cannot be empty");
            }

            List<Product> availableProducts = new ArrayList<>();

            for (Product product : products) {
                Product lockedProduct = em.find(Product.class, product.getId(), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                if (lockedProduct.isAvailable()) {
                    productRepository.decrementQuantityOfProduct(lockedProduct.getId());
                    if (lockedProduct.getQuantity() == 0) {
                        productRepository.setProductUnAvailability(lockedProduct.getId());
                    }
                    availableProducts.add(lockedProduct);
                } else {
                    System.out.println("Product " + lockedProduct.getId() + " is unavailable and will not be ordered.");
                }
            }

            if (availableProducts.isEmpty()) {
                em.getTransaction().rollback();
                throw new RuntimeException("No products are available for ordering.");
            }

            CustomerOrder customerOrder = new CustomerOrder(client, availableProducts);
            customerOrderRepository.addCustomerOrder(customerOrder);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    public void addCustomerOrder(Client client, Product product) {
        try {
            em.getTransaction().begin();
            Product lockedProduct = em.find(Product.class, product.getId(), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            if (!lockedProduct.isAvailable()) {
                em.getTransaction().rollback();
                System.out.println("Product " + lockedProduct.getId() + " is unavailable and cannot be ordered.");
                return;
            } else {
                productRepository.decrementQuantityOfProduct(lockedProduct.getId());
                if (lockedProduct.getQuantity() == 0) {
                    productRepository.setProductUnAvailability(lockedProduct.getId());
                }
            }

            CustomerOrder customerOrder = new CustomerOrder(client, lockedProduct);
            customerOrderRepository.addCustomerOrder(customerOrder);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    public List<CustomerOrder> findAllOrders() {
        return customerOrderRepository.findAllOrders();
    }

    public List<CustomerOrder> findAllClientOrders(Client client) {
        return customerOrderRepository.findAllClientOrders(client);
    }
}
