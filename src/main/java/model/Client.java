package model;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.annotations.PropertyStrategy;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@CqlName("clients")
@PropertyStrategy(mutable = true)
public class Client {
    @PartitionKey
    @CqlName("client_id")
    private UUID clientId;

    @CqlName("first_name")
    private String firstName;

    @CqlName("last_name")
    private String lastName;

    @CqlName("phone_number")
    private String phoneNumber;

    @CqlName("type")
    private String type;

    public Client(String firstName, String lastName, String phoneNumber, String type) {
        this.clientId = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.type = type;
    }

    public Client(UUID id, String firstName, String lastName, String phoneNumber, String type) {
        this.clientId = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.type = type;
    }

    public double getDiscount() {
        return switch (type.toLowerCase()) {
            case "gold" -> new ClientTypeGold().getDiscount();
            case "silver" -> new ClientTypeSilver().getDiscount();
            default -> new ClientTypeDefault().getDiscount();
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Client client = (Client) o;

        return new EqualsBuilder()
                .append(firstName, client.firstName)
                .append(lastName, client.lastName)
                .append(phoneNumber, client.phoneNumber)
                .append(type, client.type).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(firstName)
                .append(lastName)
                .append(phoneNumber)
                .append(type).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("clientId", clientId)
                .append("firstName", firstName)
                .append("lastName", lastName)
                .append("phoneNumber", phoneNumber)
                .append("type", type)
                .toString();
    }
}
