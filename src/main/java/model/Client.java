package model;

import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

@Getter
@Setter
public class Client extends AbstractEntity {

    @BsonProperty("firstname")
    private String firstName;

    @BsonProperty("lastname")
    private String lastName;

    @BsonProperty("phonenumber")
    private String phoneNumber;

    @BsonProperty("client_type")
    ClientType clientType;

    @BsonCreator
    public Client(@BsonProperty("_id") ObjectId entityId,
                  @BsonProperty("firstname") String firstName,
                  @BsonProperty("lastname") String lastName,
                  @BsonProperty("phonenumber") String phoneNumber,
                  @BsonProperty("client_type") ClientType clientType) {
        super(entityId);
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.clientType = clientType;
    }

    public Client(String firstName, String lastName, String phoneNumber, ClientType clientType) {
        super(new ObjectId());
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.clientType = clientType;
    }
}
