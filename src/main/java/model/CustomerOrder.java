package model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CustomerOrder {

    private UUID customerOrderId;

    private UUID clientId;

    private UUID productId;

    private Instant orderDate;

    private double orderPrice = 0;

    public CustomerOrder(UUID clientId, UUID productId, Instant orderDate) {
        this.customerOrderId = UUID.randomUUID();
        this.orderDate = orderDate;
        this.clientId = clientId;
        this.productId = productId;
    }

    public CustomerOrder(UUID id, UUID clientId, UUID productId, Instant orderDate, double orderPrice) {
        this.customerOrderId = id;
        this.orderDate = orderDate;
        this.clientId = clientId;
        this.productId = productId;
        this.orderPrice = orderPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CustomerOrder that = (CustomerOrder) o;

        return new EqualsBuilder()
                .append(orderPrice, that.orderPrice)
                .append(orderDate, that.orderDate)
                .append(clientId, that.clientId)
                .append(productId, that.productId).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(orderDate)
                .append(orderPrice)
                .append(clientId)
                .append(productId).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("customerOrderId", customerOrderId)
                .append("orderDate", orderDate)
                .append("orderPrice", orderPrice)
                .append("client", clientId)
                .append("product", productId)
                .toString();
    }
}