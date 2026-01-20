package com.locadora_rdt_backend.dto;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

public class RolePermissionsUpdateDTO {

    @NotEmpty(message = "Informe pelo menos uma permissão")
    private List<Long> permissionIds = new ArrayList<>();

    public RolePermissionsUpdateDTO() {}

    public List<Long> getPermissionIds() {
        return permissionIds;
    }
    public void setPermissionIds(List<Long> permissionIds) {
        this.permissionIds = permissionIds;
    }
}
