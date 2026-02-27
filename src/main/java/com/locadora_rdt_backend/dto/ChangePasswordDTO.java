package com.locadora_rdt_backend.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ChangePasswordDTO {

    @NotBlank(message = "Senha atual é obrigatória")
    private String currentPassword;

    @NotBlank(message = "Nova senha é obrigatória")
    @Size(min = 6, message = "A nova senha deve ter no mínimo 6 caracteres")
    private String newPassword;

    public ChangePasswordDTO() {}

    public ChangePasswordDTO(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}