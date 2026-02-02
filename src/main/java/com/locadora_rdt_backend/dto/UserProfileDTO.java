package com.locadora_rdt_backend.dto;

import com.locadora_rdt_backend.entities.User;

public class UserProfileDTO {
    private Long id;
    private String name;
    private String email;
    private String telephone;
    private String address;
    private boolean active;
    private boolean hasPhoto;

    public UserProfileDTO() {}

    public UserProfileDTO(User entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.email = entity.getEmail();
        this.telephone = entity.getTelephone();
        this.address = entity.getAddress();
        this.active = entity.isActive();
        this.hasPhoto = entity.getPhoto() != null && entity.getPhoto().length > 0;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getTelephone() { return telephone; }
    public String getAddress() { return address; }
    public boolean isActive() { return active; }
    public boolean isHasPhoto() { return hasPhoto; }
}
