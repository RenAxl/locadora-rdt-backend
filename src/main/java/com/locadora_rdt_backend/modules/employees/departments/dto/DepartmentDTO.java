package com.locadora_rdt_backend.modules.employees.departments.dto;

import java.io.Serializable;

public class DepartmentDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;

    public DepartmentDTO() {
    }

    public DepartmentDTO(Long id, String name) {
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