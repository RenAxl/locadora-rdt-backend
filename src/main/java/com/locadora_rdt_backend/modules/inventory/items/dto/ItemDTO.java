package com.locadora_rdt_backend.modules.inventory.items.dto;

import com.locadora_rdt_backend.modules.rental.categories.dto.CategoryDTO;
import com.locadora_rdt_backend.modules.rental.rentaltypes.dto.RentalTypeDTO;

import java.io.Serializable;
import java.math.BigDecimal;

public class ItemDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private CategoryDTO category;
    private RentalTypeDTO rentalType;
    private BigDecimal price;
    private Integer quantity;
    private Integer rentedQuantity;
    private Boolean active;

    public ItemDTO() {
        // Required by frameworks and serializers.
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CategoryDTO getCategory() {
        return category;
    }

    public RentalTypeDTO getRentalType() {
        return rentalType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getRentedQuantity() {
        return rentedQuantity;
    }

    public Boolean getActive() {
        return active;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    public void setRentalType(RentalTypeDTO rentalType) {
        this.rentalType = rentalType;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setRentedQuantity(Integer rentedQuantity) {
        this.rentedQuantity = rentedQuantity;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
