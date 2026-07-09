package com.locadora_rdt_backend.modules.inventory.items.dto;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

public class ItemUpdateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Campo requerido")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 a 100 caracteres")
    private String name;

    @NotNull(message = "Campo requerido")
    private Long categoryId;

    @NotNull(message = "Campo requerido")
    private Long rentalTypeId;

    @NotNull(message = "Campo requerido")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
    private BigDecimal price;

    @NotNull(message = "Campo requerido")
    @Min(value = 0, message = "A quantidade não pode ser negativa")
    private Integer quantity;

    public ItemUpdateDTO() {
        // Required by frameworks and serializers.
    }

    public String getName() {
        return name;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public Long getRentalTypeId() {
        return rentalTypeId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public void setRentalTypeId(Long rentalTypeId) {
        this.rentalTypeId = rentalTypeId;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
