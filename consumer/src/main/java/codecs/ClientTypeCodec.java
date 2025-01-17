package codecs;

import model.ClientType;
import model.ClientTypeDefault;
import model.ClientTypeGold;
import model.ClientTypeSilver;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

public class ClientTypeCodec implements Codec<ClientType> {

    private final CodecRegistry registry;

    public ClientTypeCodec(CodecRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void encode(BsonWriter writer, ClientType clientType, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("_clazz", clientType.getClass().getSimpleName().toLowerCase());
        writer.writeDouble("discount", clientType.getDiscount());
        writer.writeEndDocument();
    }

    @Override
    public ClientType decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        String type = reader.readString();
        double discount = reader.readDouble();
        reader.readEndDocument();

        return switch (type) {
            case "clienttypedefault" -> new ClientTypeDefault();
            case "clienttypesilver" -> new ClientTypeSilver();
            case "clienttypegold" -> new ClientTypeGold();
            default -> throw new RuntimeException("Error in decoding ClientType.");
        };
    }

    @Override
    public Class<ClientType> getEncoderClass() {
        return ClientType.class;
    }
}