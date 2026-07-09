package com.locadora_rdt_backend.modules.inventory.items.dto;

import javax.validation.constraints.DecimalMin;
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
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
    private BigDecimal price;

    public ItemUpdateDTO() {
        // Required by frameworks and serializers.
    }

    public String getName() {
        return name;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
