package com.locadora_rdt_backend.modules.employees.positions.dto;

import java.io.Serializable;

public class PositionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;

    public PositionDTO() {
    }

    public PositionDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}