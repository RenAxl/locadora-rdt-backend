package com.locadora_rdt_backend.dto;

import com.locadora_rdt_backend.entities.Role;
import com.locadora_rdt_backend.entities.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class UserDTO {

    private Long id;

    @Size(min = 5, max = 60, message = "O nome deve ter entre 5 a 60 caracteres")
    @NotBlank(message = "Campo requerido")
    private String name;

    @NotBlank(message = "Campo requerido")
    @Email(message = "favor entrar um email válido")
    private String email;

    @NotNull
    private boolean active;

    @NotBlank(message = "Campo requerido")
    private String telephone;
    private String address;
    private String photo;
    private Instant date;

    private List<RoleDTO> roles = new ArrayList<>();

    public UserDTO() {
    }

    public UserDTO(Long id, String name, String email, boolean active,
                   String telephone, String address, String photo,
                   Instant date) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.active = active;
        this.telephone = telephone;
        this.address = address;
        this.photo = photo;
        this.date = date;
    }

    public UserDTO(User entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.email = entity.getEmail();
        this.active = entity.isActive();
        this.telephone = entity.getTelephone();
        this.address = entity.getAddress();
        this.photo = entity.getPhoto();
        this.date = entity.getDate();

        for (Role role : entity.getRoles()) {
            this.roles.add(new RoleDTO(role));
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public List<RoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDTO> roles) {
        this.roles = roles;
    }
}
