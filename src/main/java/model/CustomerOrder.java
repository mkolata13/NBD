package model;

import lombok.Getter;
import lombok.Setter;
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
}
