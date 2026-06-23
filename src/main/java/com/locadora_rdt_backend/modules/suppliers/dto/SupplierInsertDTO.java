package com.locadora_rdt_backend.modules.suppliers.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class SupplierInsertDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Campo requerido")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    private String name;

    @NotBlank(message = "Campo requerido")
    @Size(min = 3, max = 100, message = "O nome fantasia deve ter entre 3 e 100 caracteres")
    private String tradeName;

    @NotBlank(message = "Campo requerido")
    @Size(min = 3, max = 150, message = "A razão social deve ter entre 3 e 150 caracteres")
    private String companyName;

    @NotBlank(message = "Campo requerido")
    @Pattern(regexp = "\\d{14}", message = "O CNPJ deve possuir 14 dígitos")
    private String cnpj;

    @NotBlank(message = "Campo requerido")
    @Size(max = 255, message = "O endereço deve possuir no máximo 255 caracteres")
    private String address;

    @NotBlank(message = "Campo requerido")
    @Email(message = "Email inválido")
    @Size(max = 100, message = "O email deve possuir no máximo 100 caracteres")
    private String email;

    @NotBlank(message = "Campo requerido")
    @Size(max = 20, message = "O telefone deve possuir no máximo 20 caracteres")
    private String phoneNumber;

    public SupplierInsertDTO() {
        // Required by frameworks and serializers.
    }

    public String getName() { return name; }
    public String getTradeName() { return tradeName; }
    public String getCompanyName() { return companyName; }
    public String getCnpj() { return cnpj; }
    public String getAddress() { return address; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }

    public void setName(String name) { this.name = name; }
    public void setTradeName(String tradeName) { this.tradeName = tradeName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public void setAddress(String address) { this.address = address; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}
