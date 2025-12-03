package com.locadora_rdt_backend.dto;

import com.locadora_rdt_backend.entities.User;

import java.time.LocalDate;

public class UserDTO {

    private Long id;
    private String name;
    private String email;
    private String password;
    private String profile;
    private String active;
    private String telephone;
    private String address;
    private String photo;
    private LocalDate date;

    public UserDTO() {
    }

    public UserDTO(Long id, String name, String email, String password, String profile,
                   String active, String telephone, String address,
                   String photo, LocalDate date) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.profile = profile;
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
        this.password = entity.getPassword();
        this.profile = entity.getProfile();
        this.active = entity.getActive();
        this.telephone = entity.getTelephone();
        this.address = entity.getAddress();
        this.photo = entity.getPhoto();
        this.date = entity.getDate();
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

}
