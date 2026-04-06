package com.locadora_rdt_backend.modules.customers.dto.file;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class CustomerFileUpdateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Nome do arquivo é obrigatório")
    private String name;

    public CustomerFileUpdateDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
