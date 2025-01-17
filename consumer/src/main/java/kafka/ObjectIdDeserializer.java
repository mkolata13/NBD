package kafka;

import org.apache.kafka.common.serialization.Deserializer;
import org.bson.types.ObjectId;

import java.util.Map;

public class ObjectIdDeserializer implements Deserializer<ObjectId> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public ObjectId deserialize(String topic, byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }

        try {
            String objectIdString = new String(data);
            return new ObjectId(objectIdString);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize ObjectId", e);
        }
    }

    @Override
    public void close() {}
}
