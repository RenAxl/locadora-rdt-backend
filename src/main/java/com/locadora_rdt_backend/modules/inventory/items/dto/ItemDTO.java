package com.locadora_rdt_backend.modules.inventory.items.dto;

import com.locadora_rdt_backend.modules.rental.categories.dto.CategoryDTO;

import java.io.Serializable;
import java.math.BigDecimal;

public class ItemDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private CategoryDTO category;
    private BigDecimal price;
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

    public BigDecimal getPrice() {
        return price;
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

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
