package model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@NoArgsConstructor
@Setter
@Getter
public abstract class ClientType {

    @BsonProperty("discount")
    protected double discount;

    @BsonCreator
    public ClientType(@BsonProperty double discount) {
        this.discount = discount;
    }

    public abstract String getTypeName();
}
