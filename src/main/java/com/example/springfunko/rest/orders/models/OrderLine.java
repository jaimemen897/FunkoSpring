package com.example.springfunko.rest.orders.models;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderLine {
    @Min(value = 1, message = "Quantity must be greater than 0")
    @Builder.Default
    private Integer quantity = 1;

    private Long idFunko;

    @Min(value = 0, message = "Price must be greater than 0")
    @Builder.Default
    private Double price = 0.0;

    @Builder.Default
    private Double total = 0.0;

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        this.total = this.price * this.quantity;
    }

    public void setPrice(Double price) {
        this.price = price;
        this.total = this.price * this.quantity;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
