import model.Client;
import model.ClientTypeDefault;
import model.ClientTypeGold;
import model.ClientTypeSilver;
import org.junit.jupiter.api.*;
import repositories.ClientRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClientTypeCodecTest {

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
    public void testClientTypes() {
        Client clientDefault = new Client("Janusz", "Kowalski", "321321321", new ClientTypeDefault());
        Client clientSilver = new Client("Janusz", "Kowalski", "321321321", new ClientTypeSilver());
        Client clientGold = new Client("Janusz", "Kowalski", "321321321", new ClientTypeGold());

        clientRepository.addClient(clientDefault);
        clientRepository.addClient(clientSilver);
        clientRepository.addClient(clientGold);

        assertEquals(ClientTypeDefault.class, clientRepository.getClientById(clientDefault.getEntityId()).getClientType().getClass());
        assertEquals(ClientTypeSilver.class, clientRepository.getClientById(clientSilver.getEntityId()).getClientType().getClass());
        assertEquals(ClientTypeGold.class, clientRepository.getClientById(clientGold.getEntityId()).getClientType().getClass());
    }
}
