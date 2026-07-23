package com.locadora_rdt_backend.modules.identity.customeraccount.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class CustomerAccountRegistrationDTO {
    @NotBlank(message = "Informe o nome")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    private String name;

    @NotBlank(message = "Informe o CPF")
    @Pattern(regexp = "\\d{11}", message = "O CPF deve conter 11 dígitos")
    private String cpf;

    @NotBlank(message = "Informe o e-mail")
    @Email(message = "Informe um e-mail válido")
    private String email;

    @NotBlank(message = "Informe o telefone")
    @Pattern(regexp = "[0-9() +\\-]{10,20}", message = "Informe um telefone válido")
    private String phone;

    @NotBlank(message = "Informe a rua")
    private String street;

    @NotBlank(message = "Informe o número")
    private String number;

    private String complement;

    @NotBlank(message = "Informe o bairro")
    private String neighborhood;

    @NotBlank(message = "Informe a cidade")
    private String city;

    @NotBlank(message = "Informe o estado")
    @Size(min = 2, max = 2, message = "O estado deve ter 2 caracteres")
    private String state;

    @NotBlank(message = "Informe o CEP")
    private String zipCode;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public String getComplement() { return complement; }
    public void setComplement(String complement) { this.complement = complement; }
    public String getNeighborhood() { return neighborhood; }
    public void setNeighborhood(String neighborhood) { this.neighborhood = neighborhood; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
}
