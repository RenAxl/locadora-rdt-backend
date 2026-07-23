package com.locadora_rdt_backend.modules.identity.users.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Embeddable
public class Address implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "street", length = 100)
    @NotBlank(message = "Campo requerido")
    private String street;

    @Column(name = "number", length = 20)
    @NotBlank(message = "Campo requerido")
    private String number;

    @Column(name = "complement", length = 100)
    private String complement;

    @Column(name = "neighborhood", length = 80)
    @NotBlank(message = "Campo requerido")
    private String neighborhood;

    @Column(name = "city", length = 80)
    @NotBlank(message = "Campo requerido")
    private String city;

    @Column(name = "state", length = 2)
    @NotBlank(message = "Campo requerido")
    private String state;

    @Column(name = "zip_code", length = 10)
    @NotBlank(message = "Campo requerido")
    private String zipCode;

    public Address() {
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getComplement() {
        return complement;
    }

    public void setComplement(String complement) {
        this.complement = complement;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
