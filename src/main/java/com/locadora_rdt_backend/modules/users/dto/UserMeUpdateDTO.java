package com.locadora_rdt_backend.modules.users.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class UserMeUpdateDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String name;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    private String telephone;
    private String address;

    public UserMeUpdateDTO() {
    }

    public UserMeUpdateDTO(String name, String email, String telephone, String address) {
        this.name = name;
        this.email = email;
        this.telephone = telephone;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}