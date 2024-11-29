package model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;


@Getter
@Setter
@NoArgsConstructor
public class Product extends AbstractEntity {

    @BsonProperty("name")
    private String name;

    @BsonProperty("baseprice")
    private double basePrice;

    @BsonProperty("weight")
    private double weight;

    @BsonProperty("quantity")
    private int quantity;

    @BsonProperty("description")
    private String description;

    @BsonProperty("isavailable")
    private boolean isAvailable;

    @BsonCreator
    public Product(@BsonProperty("_id") ObjectId entityId,
                   @BsonProperty("name")String name,
                   @BsonProperty("baseprice")double basePrice,
                   @BsonProperty("weight")double weight,
                   @BsonProperty("quantity")int quantity,
                   @BsonProperty("description")String description) {
        super(entityId);
        this.name = name;
        this.weight = weight;
        this.basePrice = basePrice;
        this.quantity = quantity;
        this.description = description;
        this.isAvailable = quantity > 0;
    }

    public Product(String name, double basePrice, double weight, int quantity, String description) {
        super(new ObjectId());
        this.name = name;
        this.weight = weight;
        this.basePrice = basePrice;
        this.quantity = quantity;
        this.description = description;
        this.isAvailable = quantity > 0;
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
                .append(quantity, product.quantity).append(isAvailable, product.isAvailable)
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
                .append(isAvailable).toHashCode();
    }
}
