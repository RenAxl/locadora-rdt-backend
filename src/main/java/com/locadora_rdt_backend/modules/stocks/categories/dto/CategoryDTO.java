package com.locadora_rdt_backend.modules.stocks.categories.dto;

import java.io.Serializable;

public class CategoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private Boolean active;
    private String imageContentType;

    public CategoryDTO() {
        // Required by frameworks and serializers.
    }

    public CategoryDTO(Long id, String name, Boolean active, String imageContentType) {
        this.id = id;
        this.name = name;
        this.active = active;
        this.imageContentType = imageContentType;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Boolean getActive() {
        return active;
    }

    public String getImageContentType() {
        return imageContentType;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }
}
