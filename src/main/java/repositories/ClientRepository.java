package repositories;

import model.Client;
import model.ClientType;
import org.bson.types.ObjectId;

import java.util.List;

public interface ClientRepository {

    void addClient(Client client);

    void deleteClient(ObjectId id);

    void setClientType(ObjectId id, ClientType clientType);

    Client getClientById(ObjectId id);

    List<Client> getAllClients();

    void dropCollection();
}
