package model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "customer_order")
public class CustomerOrder extends AbstractEntity {

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "order_price")
    private double orderPrice = 0;

    @ManyToOne
    @JoinColumn
    @NotNull
    private Client client;

    @ManyToMany
    private List<Product> products;

    public CustomerOrder(Client client, List<Product> products) {
        this.orderDate = LocalDateTime.now();
        this.client = client;
        this.products = products;
        setPrice();
        setDiscountedPrice(orderPrice);
    }

    public CustomerOrder(Client client, Product product) {
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
