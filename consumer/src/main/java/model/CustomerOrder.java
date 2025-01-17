package model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class CustomerOrder extends AbstractEntity {

    @BsonProperty("orderdate")
    private LocalDateTime orderDate;

    @BsonProperty("orderprice")
    private double orderPrice = 0;

    @BsonProperty("client")
    private Client client;

    @BsonProperty("products")
    private List<Product> products;

    @BsonCreator
    public CustomerOrder(@BsonProperty("_id") ObjectId entityId,
                         @BsonProperty("client")Client client,
                         @BsonProperty("produtcts")List<Product> products,
                         @BsonProperty("orderdate")LocalDateTime orderDate,
                         @BsonProperty("orderPrice")double orderPrice) {
        super(entityId);
        this.client = client;
        this.products = products;
        this.orderDate = orderDate;
        this.orderPrice = orderPrice;
    }

    public CustomerOrder(Client client, List<Product> products) {
        super(new ObjectId());
        this.orderDate = LocalDateTime.now();
        this.client = client;
        this.products = products;
        setPrice();
        setDiscountedPrice(orderPrice);
    }

    public CustomerOrder(Client client, Product product) {
        super(new ObjectId());
        this.orderDate = LocalDateTime.now();
        this.client = client;
        this.products = new ArrayList<>();
        if (product != null) {
            this.products.add(product);
        }
        setPrice();
        setDiscountedPrice(orderPrice);
    }

    private void setPrice() {
        if (this.products != null) {
            for (Product product : this.products) {
                this.orderPrice += product.getBasePrice();
            }
        }
    }

    private void setDiscountedPrice(double price) {
        this.orderPrice *= (1 - client.getClientType().getDiscount());
    }

    @Override
    public String toString() {
        return new org.apache.commons.lang3.builder.ToStringBuilder(this)
                .append("orderDate", orderDate)
                .append("orderPrice", orderPrice)
                .append("client", client)
                .append("products", products)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CustomerOrder that = (CustomerOrder) o;

        return new EqualsBuilder().append(orderPrice, that.orderPrice).append(orderDate, that.orderDate).append(client, that.client).append(products, that.products).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(orderDate).append(orderPrice).append(client).append(products).toHashCode();
    }
}
