package managers;

import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import model.*;
import repositories.ClientRepository;
import repositories.ClientTypeRepository;

import java.util.List;
import java.util.Set;

public class ClientManager {

    private final ClientRepository clientRepository;
    private final ClientTypeRepository clientTypeRepository;
    private final Validator validator;

    public ClientManager(EntityManager em, Validator validator) {
        this.clientRepository = new ClientRepository(em);
        this.clientTypeRepository = new ClientTypeRepository(em);
        this.validator = validator;
    }

    public Client getClient(Long id) {
        return clientRepository.findById(id);
    }

    public void addClient(String firstName, String lastName, String phoneNumber, ClientType clientType) {
        Client client = new Client(firstName, lastName, phoneNumber, clientTypeRepository.addClientType(clientType));
        Set<ConstraintViolation<Client>> violations = validator.validate(client);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<Client> violation : violations) {
                throw new RuntimeException(violation.getMessage());
            }
        }
        clientRepository.addClient(client);
    }

    public void removeClient(Long id) {
        clientRepository.removeClient(id);
    }

    public List<Client> getAllClients() {
        return clientRepository.getAll();
    }
}
