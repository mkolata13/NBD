package repositories;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import model.Client;
import model.ClientType;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class ClientRepository extends AbstractMongoRepository {
    private final MongoCollection<Client> clientsCollection;

    public ClientRepository() {
        this.clientsCollection = getMongoDatabase().getCollection("clients", Client.class);
    }

    public void addClient(Client client) {
        clientsCollection.insertOne(client);
    }

    public void deleteClient(ObjectId id) {
        clientsCollection.findOneAndDelete(Filters.eq("_id", id));
    }

    public void setClientType(ObjectId id, ClientType clientType) {
        Bson filter = Filters.eq("_id", id);

        Bson update = Updates.set("client_type",
                new Document("_clazz", clientType.getClass().getSimpleName().toLowerCase())
                        .append("discount", clientType.getDiscount())
        );

        UpdateResult result = clientsCollection.updateOne(filter, update);

        if (result.getModifiedCount() == 0) {
            System.out.println("Client type could not be updated. Client may not exist.");
        } else {
            System.out.println("Client type updated successfully.");
        }
    }


    public Client getClientById(ObjectId id) {
        return clientsCollection.find(Filters.eq("_id", id)).first();
    }

    public List<Client> getAllClients() {
        return clientsCollection.find().into(new ArrayList<>());
    }

    public void dropCollection() {
        clientsCollection.drop();
    }
}
