package com.locadora_rdt_backend.dto;

import com.locadora_rdt_backend.entities.Permission;
import com.locadora_rdt_backend.entities.Role;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RoleDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String authority;
    private List<PermissionDTO> permissions = new ArrayList<>();

    public RoleDTO() {}

    public RoleDTO(Long id, String authority) {
        this.id = id;
        this.authority = authority;
    }

    public RoleDTO(Role entity) {
        this.id = entity.getId();
        this.authority = entity.getAuthority();
        for (Permission p : entity.getPermissions()) {
            this.permissions.add(new PermissionDTO(p));
        }
    }

    public Long getId() {
        return id;
    }

    public String getAuthority() {
        return authority;
    }

    public List<PermissionDTO> getPermissions() {
        return permissions;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public void setPermissions(List<PermissionDTO> permissions) {
        this.permissions = permissions;
    }
}
