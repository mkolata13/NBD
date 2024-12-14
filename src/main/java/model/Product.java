package model;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.annotations.PropertyStrategy;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@Entity
@CqlName("products")
@PropertyStrategy(mutable = true)
public class Product {
    @PartitionKey
    @CqlName("product_id")
    private UUID productId;

    @CqlName("name")
    private String name;

    @CqlName("base_price")
    private double basePrice;

    @CqlName("weight")
    private double weight;

    @CqlName("quantity")
    private int quantity;

    @CqlName("description")
    private String description;

    @CqlName("available")
    private boolean available;

    public Product(String name, double basePrice, double weight, int quantity, String description) {
        this.productId = UUID.randomUUID();
        this.name = name;
        this.weight = weight;
        this.basePrice = basePrice;
        this.quantity = quantity;
        this.description = description;
        this.available = quantity > 0;
    }

    public Product(UUID id, String name, double basePrice, double weight, int quantity, String description, boolean available) {
        this.productId = id;
        this.name = name;
        this.weight = weight;
        this.basePrice = basePrice;
        this.quantity = quantity;
        this.description = description;
        this.available = available;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Product product = (Product) o;

        return new org.apache.commons.lang3.builder.EqualsBuilder()
                .append(basePrice, product.basePrice).append(weight, product.weight)
                .append(quantity, product.quantity).append(available, product.available)
                .append(name, product.name)
                .append(description, product.description).isEquals();
    }

    @Override
    public int hashCode() {
        return new org.apache.commons.lang3.builder.HashCodeBuilder(17, 37)
                .append(name)
                .append(basePrice)
                .append(weight)
                .append(quantity)
                .append(description)
                .append(available).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("productId", productId)
                .append("name", name)
                .append("basePrice", basePrice)
                .append("weight", weight)
                .append("quantity", quantity)
                .append("description", description)
                .append("available", available)
                .toString();
    }
}
