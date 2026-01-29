package com.locadora_rdt_backend.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class NewPasswordDTO {

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
    private String password;

    public NewPasswordDTO() {}

    public NewPasswordDTO(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
