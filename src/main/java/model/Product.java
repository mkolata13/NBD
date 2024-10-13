package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Product extends AbstractEntity{

    @Column
    @NotNull
    private String name;

    @Column(name = "base_price")
    @NotNull
    private double basePrice;

    @Column
    @NotNull
    private double weight;

    @Column
    @NotNull
    @Min(value = 0)
    private int quantity;

    @Column
    @NotNull
    private String description;

    @Column(name = "is_available")
    @NotNull
    private boolean isAvailable = true;

    public Product(String name, double basePrice, double weight, int quantity, String description) {
        this.name = name;
        this.weight = weight;
        this.basePrice = basePrice;
        this.quantity = quantity;
        this.description = description;
    }
}
