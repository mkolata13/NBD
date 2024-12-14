package model;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Entity
@CqlName("client_type")
public class ClientType {
    @PartitionKey
    private String discriminator;

    @CqlName("discount")
    protected double discount;

    public ClientType(String discriminator, double discount) {
        this.discriminator = discriminator;
        this.discount = discount;
    }
}
