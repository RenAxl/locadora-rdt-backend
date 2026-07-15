package com.locadora_rdt_backend.modules.rentaltypes.dto;

import java.io.Serializable;

public class RentalTypeDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String type;
    private Boolean active;

    public RentalTypeDTO() {
        // Required by frameworks and serializers.
    }

    public RentalTypeDTO(Long id, String name, String type, Boolean active) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
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

    public void setType(String type) {
        this.type = type;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
