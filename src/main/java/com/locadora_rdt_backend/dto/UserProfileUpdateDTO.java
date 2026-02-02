package com.locadora_rdt_backend.dto;

import javax.validation.constraints.NotBlank;

public class UserProfileUpdateDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String name;

    @NotBlank(message = "Telefone é obrigatório")
    private String telephone;

    @NotBlank(message = "Endereço é obrigatório")
    private String address;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
