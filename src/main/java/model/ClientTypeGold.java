package model;

import jakarta.persistence.*;

@Entity
@Access(AccessType.FIELD)
@DiscriminatorValue("gold")
public class ClientTypeGold extends ClientType {

    public ClientTypeGold() {
        this.setDiscount(0.5);
    }
}
