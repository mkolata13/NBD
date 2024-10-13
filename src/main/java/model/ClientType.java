package model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "client_type")
public abstract class ClientType extends AbstractEntity {

    @Column(name = "discount")
    @Min(value = 0, message = "Discount must be at least 0.")
    @Max(value = 1, message = "Discount cannot exceed 1 (100%).")
    protected double discount;

    public void setDiscount(double discount) {
        if (discount < 0 || discount > 1) {
            throw new IllegalArgumentException("Discount has to be between 0 and 1 (0 - 100%)");
        }
        this.discount = discount;
    }

    public double getDiscount() {
        return this.discount;
    }
}
