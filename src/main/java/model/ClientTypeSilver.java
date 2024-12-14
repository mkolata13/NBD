package model;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;

@CqlName("client_type")
public class ClientTypeSilver extends ClientType {

    public ClientTypeSilver() {
        super("silver", 0.2);
    }
}
