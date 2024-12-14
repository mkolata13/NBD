package providers;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import model.ClientType;
import model.ClientTypeDefault;
import model.ClientTypeGold;
import model.ClientTypeSilver;

public class ClientTypeProvider {
    CqlSession session;

    ClientTypeProvider(CqlSession session){
        this.session = session;
    }

    public void create(ClientType clientType) {
        createType(clientType);
    }

    public void  createType(ClientType clientType) {
        String insertQuery = "INSERT INTO shop.client_type ( discriminator, discount) VALUES (?, ?)";
        PreparedStatement preparedStatement = session.prepare(insertQuery);
        switch (clientType.getDiscriminator()) {
            case "default" -> {
                ClientTypeDefault typeDefault = (ClientTypeDefault) clientType;
                session.execute(preparedStatement.bind(typeDefault.getDiscriminator(), typeDefault.getDiscount()));
            }
            case "silver" -> {
                ClientTypeSilver typeSilver = (ClientTypeSilver) clientType;
                session.execute(preparedStatement.bind(typeSilver.getDiscriminator(), typeSilver.getDiscount()));
            }
            case "gold" -> {
                ClientTypeGold typeGold = (ClientTypeGold) clientType;
                session.execute(preparedStatement.bind(typeGold.getDiscriminator(), typeGold.getDiscount()));
            }
            default -> throw new IllegalArgumentException("Unknown client type discriminator: " + clientType.getDiscriminator());
        }
    }
}
