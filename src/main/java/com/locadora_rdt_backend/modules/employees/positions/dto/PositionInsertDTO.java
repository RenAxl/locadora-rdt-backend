package com.locadora_rdt_backend.modules.employees.positions.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class PositionInsertDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Campo requerido")
    @Size(min = 3, max = 60, message = "O nome deve ter entre 3 a 60 caracteres")
    private String name;

    public PositionInsertDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}