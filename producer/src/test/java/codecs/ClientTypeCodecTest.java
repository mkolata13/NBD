package codecs;

import model.Client;
import model.ClientTypeDefault;
import model.ClientTypeGold;
import model.ClientTypeSilver;
import org.junit.jupiter.api.*;
import repositories.ClientRepository;
import repositories.mongodb.ClientRepositoryMongo;

import static org.junit.jupiter.api.Assertions.assertSame;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClientTypeCodecTest {

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
    public void testClientTypes() {
        Client clientDefault = new Client("Janusz", "Kowalski", "321321321", new ClientTypeDefault());
        Client clientSilver = new Client("Janusz", "Kowalski", "321321321", new ClientTypeSilver());
        Client clientGold = new Client("Janusz", "Kowalski", "321321321", new ClientTypeGold());

        clientRepository.addClient(clientDefault);
        clientRepository.addClient(clientSilver);
        clientRepository.addClient(clientGold);

        assertSame(ClientTypeDefault.class, clientRepository.getClientById(clientDefault.getEntityId()).getClientType().getClass());
        assertSame(ClientTypeSilver.class, clientRepository.getClientById(clientSilver.getEntityId()).getClientType().getClass());
        assertSame(ClientTypeGold.class, clientRepository.getClientById(clientGold.getEntityId()).getClientType().getClass());
    }
}
