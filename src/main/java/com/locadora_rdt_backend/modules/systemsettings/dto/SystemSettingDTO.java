package com.locadora_rdt_backend.modules.systemsettings.dto;

import com.locadora_rdt_backend.modules.systemsettings.model.Address;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class SystemSettingDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank(message = "Nome da locadora é obrigatório")
    @Size(max = 120, message = "Nome da locadora deve ter no máximo 120 caracteres")
    private String companyName;

    @Valid
    @NotNull(message = "Endereço é obrigatório")
    private Address address;

    public SystemSettingDTO() {}

    public SystemSettingDTO(Long id, String companyName, Address address) {
        this.id = id;
        this.companyName = companyName;
        this.address = address;
    }

    public Long getId() { return id; }
    public String getCompanyName() { return companyName; }
    public Address getAddress() { return address; }
    public void setId(Long id) { this.id = id; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setAddress(Address address) { this.address = address; }
}
