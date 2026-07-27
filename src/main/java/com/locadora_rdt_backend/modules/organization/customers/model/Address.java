package com.locadora_rdt_backend.modules.organization.customers.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Embeddable
public class Address implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Campo requerido")
    @Column(name = "street", length = 100)
    private String street;

    @NotBlank(message = "Campo requerido")
    @Column(name = "number", length = 20)
    private String number;

    @Column(name = "complement", length = 100)
    private String complement;

    @NotBlank(message = "Campo requerido")
    @Column(name = "neighborhood", length = 80)
    private String neighborhood;

    @NotBlank(message = "Campo requerido")
    @Column(name = "city", length = 80)
    private String city;

    @NotBlank(message = "Campo requerido")
    @Column(name = "state", length = 2)
    private String state;

    @NotBlank(message = "Campo requerido")
    @Column(name = "zip_code", length = 10)
    private String zipCode;

    public Address() {
        // Required by frameworks and serializers.
    }

    public String getStreet() { return street; }
    public String getNumber() { return number; }
    public String getComplement() { return complement; }
    public String getNeighborhood() { return neighborhood; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getZipCode() { return zipCode; }

    public void setStreet(String street) { this.street = street; }
    public void setNumber(String number) { this.number = number; }
    public void setComplement(String complement) { this.complement = complement; }
    public void setNeighborhood(String neighborhood) { this.neighborhood = neighborhood; }
    public void setCity(String city) { this.city = city; }
    public void setState(String state) { this.state = state; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
}
