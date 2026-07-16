package com.locadora_rdt_backend.modules.rental.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class ShippingCalculationDTO {
    @NotBlank(message = "Rua é obrigatória") private String street;
    @NotBlank(message = "Número é obrigatório") private String number;
    private String neighborhood;
    @NotBlank(message = "Cidade é obrigatória") private String city;
    @NotBlank(message = "UF é obrigatória") private String state;
    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP inválido")
    private String zipCode;

    public String getStreet() { return street; }
    public String getNumber() { return number; }
    public String getNeighborhood() { return neighborhood; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getZipCode() { return zipCode; }
    public void setStreet(String street) { this.street = street; }
    public void setNumber(String number) { this.number = number; }
    public void setNeighborhood(String neighborhood) { this.neighborhood = neighborhood; }
    public void setCity(String city) { this.city = city; }
    public void setState(String state) { this.state = state; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
}
