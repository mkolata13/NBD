package model;

import jakarta.json.bind.annotation.JsonbTypeAdapter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import redis.ObjectIdAdapter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractEntity implements Serializable {
    @BsonProperty("_id")
    @JsonbTypeAdapter(ObjectIdAdapter.class)
    private ObjectId entityId;

    public AbstractEntity(ObjectId entityId) {
        this.entityId = entityId;
    }
}
