package repositories;

import codecs.CustomCodecProvider;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.Setter;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.jsr310.Jsr310CodecProvider;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.List;


@Getter
@Setter
public abstract class AbstractMongoRepository implements AutoCloseable {
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    private ConnectionString connectionString = new ConnectionString(
            "mongodb://mongodb1:27017,mongodb2:27018,mongodb3:27019/?replicaSet=replica_set_single");
    private MongoCredential credential = MongoCredential.createCredential(
            "admin", "admin", "adminpassword".toCharArray());

    private CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(
            PojoCodecProvider.builder()
                    .automatic(true)
                    .conventions(List.of(Conventions.ANNOTATION_CONVENTION))
                    .build());

    private void initDbConnection() {
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .credential(credential)
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .codecRegistry(CodecRegistries.fromRegistries(
                        CodecRegistries.fromProviders(new CustomCodecProvider()),
                        CodecRegistries.fromProviders(new Jsr310CodecProvider()),
                        CodecRegistries.fromProviders(PojoCodecProvider.builder().build()),
                        MongoClientSettings.getDefaultCodecRegistry(),
                        pojoCodecRegistry
                ))
                .build();
        mongoClient = MongoClients.create(settings);
        mongoDatabase = mongoClient.getDatabase("shop");
    }

    public AbstractMongoRepository() {
        initDbConnection();
    }

    public ClientSession getClientSession() {
        return mongoClient.startSession();
    }

    @Override
    public void close() {
        mongoDatabase.drop();
        mongoClient.close();
    }
}
