package com.locadora_rdt_backend.dto;

public class RoleListDTO {

    private Long id;
    private String authority;
    private Long permissionsCount;

    public RoleListDTO(Long id, String authority, Long permissionsCount) {
        this.id = id;
        this.authority = authority;
        this.permissionsCount = permissionsCount;
    }

    public Long getId() {
        return id;
    }

    public String getAuthority() {
        return authority;
    }

    public Long getPermissionsCount() {
        return permissionsCount;
    }
}

