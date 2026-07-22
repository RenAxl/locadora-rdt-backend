package com.locadora_rdt_backend.modules.customeraccount.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class CustomerAccountResendDTO {
    @NotBlank(message = "Informe o e-mail")
    @Email(message = "Informe um e-mail válido")
    private String email;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
