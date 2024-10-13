package model;

import jakarta.persistence.*;

@Entity
@Access(AccessType.FIELD)
@DiscriminatorValue("silver")
public class ClientTypeSilver extends ClientType {

    public ClientTypeSilver() {
        this.setDiscount(0.2);
    }
}
