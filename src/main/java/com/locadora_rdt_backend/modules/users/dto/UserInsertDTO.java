package com.locadora_rdt_backend.modules.users.dto;

import com.locadora_rdt_backend.modules.users.validation.UserInsertValid;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@UserInsertValid
public class UserInsertDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Size(min = 5, max = 60, message = "O nome deve ter entre 5 e 60 caracteres")
    @NotBlank(message = "Campo requerido")
    private String name;

    @NotBlank(message = "Campo requerido")
    @Email(message = "Favor informar um email válido")
    private String email;

    @NotBlank(message = "Campo requerido")
    private String telephone;

    private String address;

    @NotEmpty(message = "O usuário deve possuir ao menos um perfil")
    private List<Long> roleIds = new ArrayList<>();

    public UserInsertDTO() {
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

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }
}