package com.locadora_rdt_backend.modules.identity.customeraccount.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CustomerAccountCreatePasswordDTO {
    @NotBlank(message = "Informe a nova senha")
    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
    private String password;

    @NotBlank(message = "Confirme a nova senha")
    private String passwordConfirmation;

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPasswordConfirmation() { return passwordConfirmation; }
    public void setPasswordConfirmation(String passwordConfirmation) { this.passwordConfirmation = passwordConfirmation; }
}
