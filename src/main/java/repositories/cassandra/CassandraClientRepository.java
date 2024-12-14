package repositories.cassandra;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import dao.ClientDao;
import mappers.ClientMapper;
import mappers.ClientMapperBuilder;
import model.Client;
import model.ClientTypeDefault;
import model.ClientTypeGold;
import model.ClientTypeSilver;
import providers.ClientTypeRepository;
import repositories.AbstractCassandraRepository;
import repositories.ClientRepository;

import java.util.UUID;

public class CassandraClientRepository extends AbstractCassandraRepository implements ClientRepository {
    private final ClientDao clientDao;

    public CassandraClientRepository() {
        initSession();
        CqlSession session = getSession();
        SimpleStatement createClients = SchemaBuilder.createTable(CqlIdentifier.fromCql("clients"))
                .ifNotExists()
                .withPartitionKey(CqlIdentifier.fromCql("client_id"), DataTypes.UUID)
                .withColumn(CqlIdentifier.fromCql("first_name"), DataTypes.TEXT)
                .withColumn(CqlIdentifier.fromCql("last_name"), DataTypes.TEXT)
                .withColumn(CqlIdentifier.fromCql("phone_number"), DataTypes.TEXT)
                .withColumn(CqlIdentifier.fromCql("type"), DataTypes.TEXT)
                .build();
        session.execute(createClients);
        ClientMapper clientMapper = new ClientMapperBuilder(session).build();
        this.clientDao = clientMapper.clientDao();
        ClientTypeRepository clientTypeRepository = new ClientTypeRepository(session);
        if(!clientTypeRepository.findByType("default")){
            clientTypeRepository.create(new ClientTypeDefault());
        }
        if(!clientTypeRepository.findByType("silver")){
            clientTypeRepository.create(new ClientTypeSilver());
        }
        if(!clientTypeRepository.findByType("gold")){
            clientTypeRepository.create(new ClientTypeGold());
        }
    }

    @Override
    public boolean addClient(Client client) {
        return clientDao.create(client);
    }

    @Override
    public boolean deleteClient(Client client) {
        return clientDao.delete(client);
    }

    @Override
    public void updateClient(Client client) {
        if (client != null) {
            clientDao.update(client);
        }
    }

    @Override
    public Client getClientById(UUID id) {
        return clientDao.getById(id);
    }

    @Override
    public void close() {
        getSession().close();
    }
}
