package kafka;

import org.apache.kafka.common.serialization.Serializer;
import org.bson.types.ObjectId;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ObjectIdSerializer implements Serializer<ObjectId> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public byte[] serialize(String topic, ObjectId data) {
        return data == null ? null : data.toHexString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void close() {}
}
