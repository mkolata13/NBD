package repositories;

import jakarta.persistence.EntityManager;
import model.*;


public class ClientTypeRepository {

    private final EntityManager em;

    public ClientTypeRepository(EntityManager em) {
        this.em = em;
    }

    public ClientType addClientType(ClientType clientType) {
        if (clientType.getId() == null) {
            em.persist(clientType);
        } else {
            em.merge(clientType);
        }
        return clientType;
    }
}
