package repositories;

import jakarta.persistence.EntityManager;
import model.*;


public class ClientTypeRepository {

    private final EntityManager em;

    public ClientTypeRepository(EntityManager em) {
        this.em = em;
    }

    public ClientType addClientType(ClientType clientType) {
        try {
            em.getTransaction().begin();
            if (clientType.getId() == null) {
                em.persist(clientType);
            } else {
                clientType = em.merge(clientType);
            }
            em.getTransaction().commit();
            em.detach(clientType);
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }

        return clientType;
    }
}
