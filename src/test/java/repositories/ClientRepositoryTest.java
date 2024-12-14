package repositories;

import model.Client;
import org.junit.jupiter.api.*;
import repositories.cassandra.CassandraClientRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClientRepositoryTest {

    private ClientRepository clientRepository;

    @BeforeAll
    public void setup() {
        clientRepository = new CassandraClientRepository();
    }

    @Test
    public void testAddClient() {
        Client client = new Client("John", "Michael", "321321321", "silver");
        assertThat(clientRepository.addClient(client), equalTo(true));
    }

    @Test
    public void testDeleteClient() {
        Client client = new Client("John", "Michael", "321321321", "default");
        clientRepository.addClient(client);

        assertThat(clientRepository.deleteClient(client), equalTo(true));
    }

    @Test
    public void testGetClientById() {
        Client client = new Client("John", "Michael", "321321321", "gold");
        clientRepository.addClient(client);

        assertThat(clientRepository.getClientById(client.getClientId()), equalTo(client));
    }

    @Test
    public void testUpdateClient() {
        Client client = new Client("John", "Michael", "321321321", "gold");
        clientRepository.addClient(client);

        client.setType("default");
        clientRepository.updateClient(client);

        assertThat(clientRepository.getClientById(client.getClientId()), equalTo(client));
    }
}