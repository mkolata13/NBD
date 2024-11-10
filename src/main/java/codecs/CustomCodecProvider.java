package codecs;

import model.ClientType;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class CustomCodecProvider implements CodecProvider {

    public CustomCodecProvider() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if (clazz == ClientType.class) {
            return (Codec<T>) new ClientTypeCodec(registry);
        }
        // return null when there is no provider for the requested class
        return null;
    }
}