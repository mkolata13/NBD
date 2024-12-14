package repositories;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.schema.CreateKeyspace;

import java.net.InetSocketAddress;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createKeyspace;

public abstract class AbstractCassandraRepository implements AutoCloseable {
    private static CqlSession session;

    public void initSession() {
        session = CqlSession.builder()
                .addContactPoint(new InetSocketAddress("cassandra1", 9042))
                .addContactPoint(new InetSocketAddress("cassandra2", 9043))
                .withLocalDatacenter("dc1")
                .withAuthCredentials("cassandra", "cassandrapassword")
                .withKeyspace(CqlIdentifier.fromCql("shop")) // zakomentuj przy tworzeniu bazy, potem odkomentuj
                .build();

        CreateKeyspace keyspace = createKeyspace(CqlIdentifier.fromCql("shop"))
                .ifNotExists()
                .withSimpleStrategy(2)
                .withDurableWrites(true);

        SimpleStatement createKeyspaceStatement = keyspace.build();
        session.execute(createKeyspaceStatement);
    }

    public CqlSession getSession() {
        return session;
    }

    @Override
    public void close() {
        session.close();
    }
}
