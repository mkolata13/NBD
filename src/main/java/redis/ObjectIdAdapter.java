package redis;

import jakarta.json.bind.adapter.JsonbAdapter;
import org.bson.types.ObjectId;

public class ObjectIdAdapter implements JsonbAdapter<ObjectId, String> {

    @Override
    public String adaptToJson(ObjectId obj) {
        return obj.toHexString();
    }

    @Override
    public ObjectId adaptFromJson(String obj) {
        return new ObjectId(obj);
    }
}
