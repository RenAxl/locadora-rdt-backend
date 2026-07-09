package com.locadora_rdt_backend.modules.inventory.items.dto;

import com.locadora_rdt_backend.modules.rental.categories.dto.CategoryDTO;
import com.locadora_rdt_backend.modules.rental.rentaltypes.dto.RentalTypeDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

public class ItemDetailsDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long version;
    private String name;
    private CategoryDTO category;
    private RentalTypeDTO rentalType;
    private BigDecimal price;
    private Integer quantity;
    private Integer rentedQuantity;
    private Boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    public ItemDetailsDTO() {
        // Required by frameworks and serializers.
    }

    public Long getId() {
        return id;
    }

    public Long getVersion() {
        return version;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setVersion(Long version) {
        this.version = version;
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

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
