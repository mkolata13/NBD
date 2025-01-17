package repositories;

import model.Client;
import model.ClientTypeGold;
import model.ClientTypeSilver;
import org.junit.jupiter.api.*;
import repositories.mongodb.ClientRepositoryMongo;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertSame;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClientRepositoryTest {

    private ClientRepository clientRepository;

    @BeforeAll
    public void setup() {
        clientRepository = new ClientRepositoryMongo();
    }

    @BeforeEach
    @AfterAll
    public void cleanup() {
        clientRepository.dropCollection();
    }

    @Test
    public void testAddClient() {
        Client client = new Client("John", "Michael", "321321321", new ClientTypeSilver());
        clientRepository.addClient(client);

        List<Client> clients = clientRepository.getAllClients();
        assertThat(clients, hasSize(1));
        assertThat(clients, hasItem(hasProperty("entityId", is(client.getEntityId()))));
    }

    @Test
    public void testDeleteClient() {
        Client client = new Client("John", "Michael", "321321321", new ClientTypeSilver());
        Client deletedClient = new Client("Tom", "Michael", "123123123", new ClientTypeGold());

        clientRepository.addClient(client);
        clientRepository.addClient(deletedClient);
        assertThat(clientRepository.getAllClients(), hasSize(2));

        clientRepository.deleteClient(deletedClient.getEntityId());
        List<Client> clients = clientRepository.getAllClients();

        assertThat(clients, hasSize(1));
        assertThat(clients, hasItem(hasProperty("entityId", is(client.getEntityId()))));
    }

    @Test
    public void testSetClientType() {
        Client client = new Client("John", "Michael", "321321321", new ClientTypeSilver());
        clientRepository.addClient(client);

        clientRepository.setClientType(client.getEntityId(), new ClientTypeGold());

        assertSame(ClientTypeGold.class, clientRepository.getClientById(client.getEntityId()).getClientType().getClass());
    }
}