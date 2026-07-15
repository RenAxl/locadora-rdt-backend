package com.locadora_rdt_backend.modules.rentaltypes.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
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

    @NotNull(message = "Campo requerido")
    @Min(value = 1, message = "A quantidade de dias deve ser maior que zero")
    private Integer days;

    public RentalTypeUpdateDTO() {
        // Required by frameworks and serializers.
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Integer getDays() {
        return days;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDays(Integer days) {
        this.days = days;
    }
}
