package model;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;

@CqlName("client_type")
public class ClientTypeDefault extends ClientType {

    public ClientTypeDefault() {
        super("default", 0.0);
    }
}
