package repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import model.Client;

import java.util.List;

public class ClientRepository {

    private final EntityManager em;

    public ClientRepository(EntityManager em) {
        this.em = em;
    }

    public void addClient(Client client) {
        try {
            em.getTransaction().begin();
            if (client.getId() == null) {
                em.persist(client);
            } else {
                client = em.merge(client);
            }
            em.getTransaction().commit();
            em.detach(client);
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    public void removeClient(Long id) {
        try {
            em.getTransaction().begin();
            Client client = findById(id);
            em.remove(client);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    public Client findById(Long id) {
        return em.find(Client.class, id);
    }

    public List<Client> getAll() {
        TypedQuery<Client> query = em.createQuery("Select c from Client c", Client.class);
        return query.getResultList();
    }
}
