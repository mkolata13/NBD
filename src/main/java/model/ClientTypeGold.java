package model;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;

@CqlName("client_type")
public class ClientTypeGold extends ClientType {

    public ClientTypeGold() {
        super("gold", 0.5);
    }
}
