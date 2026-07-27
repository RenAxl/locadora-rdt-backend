package com.locadora_rdt_backend.modules.organization.suppliers.dto;

import com.locadora_rdt_backend.modules.organization.suppliers.model.Address;

import java.io.Serializable;

public class SupplierDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String tradeName;
    private String companyName;
    private String cnpj;
    private Address address;
    private String email;
    private String phoneNumber;
    private String imageContentType;

    public SupplierDTO() {
        // Required by frameworks and serializers.
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getTradeName() { return tradeName; }
    public String getCompanyName() { return companyName; }
    public String getCnpj() { return cnpj; }
    public Address getAddress() { return address; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getImageContentType() { return imageContentType; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setTradeName(String tradeName) { this.tradeName = tradeName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public void setAddress(Address address) { this.address = address; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setImageContentType(String imageContentType) { this.imageContentType = imageContentType; }
}
