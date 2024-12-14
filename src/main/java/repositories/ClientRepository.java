package repositories;

import model.Client;

import java.util.UUID;

public interface ClientRepository {

    boolean addClient(Client client);

    boolean deleteClient(Client client);

    void updateClient(Client client);

    Client getClientById(UUID id);
}
