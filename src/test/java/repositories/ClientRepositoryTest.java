package repositories;

import model.Client;
import model.ClientTypeGold;
import model.ClientTypeSilver;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClientRepositoryTest {

    private ClientRepository clientRepository;

    @BeforeAll
    public void setup() {
        clientRepository = new ClientRepository();
    }

    @BeforeEach
    public void cleanup() {
        clientRepository.getAllClients().forEach(client -> clientRepository.deleteClient(client.getEntityId()));
    }

    @Test
    public void testAddClient() {
        Client client = new Client("John", "Michael", "321321321", new ClientTypeSilver());
        clientRepository.addClient(client);

        List<Client> clients = clientRepository.getAllClients();
        assertEquals(1, clients.size());
        assertEquals(client.getEntityId(), clients.get(0).getEntityId());
    }

    @Test
    public void testDeleteClient() {
        Client client = new Client("John", "Michael", "321321321", new ClientTypeSilver());
        Client deletedClient = new Client("Tom", "Michael", "123123123", new ClientTypeGold());

        clientRepository.addClient(client);
        clientRepository.addClient(deletedClient);
        assertEquals(2, clientRepository.getAllClients().size());

        clientRepository.deleteClient(deletedClient.getEntityId());
        List<Client> clients = clientRepository.getAllClients();

        assertEquals(1, clients.size());
        assertEquals(client.getEntityId(), clients.get(0).getEntityId());
    }

    @Test
    public void testSetClientType() {
        Client client = new Client("John", "Michael", "321321321", new ClientTypeSilver());
        clientRepository.addClient(client);

        clientRepository.setClientType(client.getEntityId(), new ClientTypeGold());

        assertEquals(ClientTypeGold.class, clientRepository.getClientById(client.getEntityId()).getClientType().getClass());
    }
}