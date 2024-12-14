package providers;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import model.ClientType;

import java.util.List;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;

public class ClientTypeRepository {
    private static CqlSession session;
    ClientTypeProvider clientTypeProvider;

    public ClientTypeRepository(CqlSession session) {
        ClientTypeRepository.session = session;
        clientTypeProvider = new ClientTypeProvider(session);
        createClientTypeTable();
    }

    private static void createClientTypeTable() {
        session.execute(SchemaBuilder.createTable(CqlIdentifier.fromCql("client_type"))
                .ifNotExists()
                .withPartitionKey(CqlIdentifier.fromCql("discriminator"), DataTypes.TEXT)
                .withColumn(CqlIdentifier.fromCql("discount"), DataTypes.DOUBLE)
                .build());

    }
    public void create(ClientType clientType){
        clientTypeProvider.create(clientType);
    }

    public boolean findByType(String type){
        Select select = QueryBuilder.selectFrom("client_type").all()
                .where(Relation.column("discriminator").isEqualTo(literal(type)));
        ResultSet resultSet = session.execute(select.build());
        List<Row> result = resultSet.all();
        return !result.isEmpty();
    }
}
