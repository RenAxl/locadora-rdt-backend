package com.locadora_rdt_backend.dto;

import com.locadora_rdt_backend.entities.Permission;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class PermissionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    @Size(min = 5, max = 15, message = "O nome deve ter entre 5 a 15 caracteres")
    @NotBlank(message = "Campo requerido")
    private String name;

    @NotBlank(message = "Campo requerido")
    private String groupName;

    public PermissionDTO() {}

    public PermissionDTO(Long id, String name, String groupName) {
        this.id = id;
        this.name = name;
        this.groupName = groupName;
    }

    public PermissionDTO(Permission entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.groupName = entity.getGroupName();
    }

    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
