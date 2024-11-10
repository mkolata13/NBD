package managers;


import model.Client;
import model.ClientType;
import org.bson.types.ObjectId;
import repositories.ClientRepository;

import java.util.List;

public class ClientManager {

    private final ClientRepository clientRepository;

    public ClientManager() {
        this.clientRepository = new ClientRepository();
    }

    public void addClient(String firstName, String lastName, String phoneNumber, ClientType clientType) {
        Client client = new Client(firstName, lastName, phoneNumber, clientType);
        clientRepository.addClient(client);
    }

    public Client getClient(ObjectId clientId) {
        return clientRepository.getClientById(clientId);
    }

    public List<Client> getAllClients() {
        return clientRepository.getAllClients();
    }
}
