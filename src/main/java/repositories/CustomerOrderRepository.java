package repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import model.Client;
import model.CustomerOrder;

import java.util.List;

public class CustomerOrderRepository {

    private final EntityManager em;

    public CustomerOrderRepository(EntityManager em) {
        this.em = em;
    }

    public void addCustomerOrder(CustomerOrder customerOrder) {
        if (customerOrder.getId() == null) {
            em.persist(customerOrder);
        } else {
            em.merge(customerOrder);
        }
    }

    public CustomerOrder findById(Long id) {
        return em.find(CustomerOrder.class, id);
    }

    public List<CustomerOrder> findAllClientOrders(Client client) {
        TypedQuery<CustomerOrder> query = em.createQuery("SELECT c FROM CustomerOrder c WHERE c.client = :client", CustomerOrder.class);
        query.setParameter("client", client);
        return query.getResultList();
    }

    public List<CustomerOrder> findAllOrders() {
        TypedQuery<CustomerOrder> query = em.createQuery("Select c from CustomerOrder c", CustomerOrder.class);
        return query.getResultList();
    }
}
