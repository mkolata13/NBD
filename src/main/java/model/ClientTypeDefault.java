package model;

import jakarta.persistence.*;

@Entity
@Access(AccessType.FIELD)
@DiscriminatorValue("default")
public class ClientTypeDefault extends ClientType {

    public ClientTypeDefault() {
        this.setDiscount(0.0);
    }
}
