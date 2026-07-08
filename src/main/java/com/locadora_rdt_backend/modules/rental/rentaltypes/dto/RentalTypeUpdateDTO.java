package com.locadora_rdt_backend.modules.rental.rentaltypes.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class RentalTypeUpdateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Campo requerido")
    @Size(min = 3, max = 60, message = "O nome deve ter entre 3 a 60 caracteres")
    private String name;

    @NotBlank(message = "Campo requerido")
    @Size(min = 2, max = 30, message = "O tipo deve ter entre 2 a 30 caracteres")
    private String type;

    public RentalTypeUpdateDTO() {
        // Required by frameworks and serializers.
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }
}
