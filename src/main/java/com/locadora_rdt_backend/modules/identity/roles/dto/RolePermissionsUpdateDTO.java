package com.locadora_rdt_backend.modules.identity.roles.dto;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

public class RolePermissionsUpdateDTO {

    @NotEmpty(message = "Informe pelo menos uma permissão")
    private List<Long> permissionIds = new ArrayList<>();

    public RolePermissionsUpdateDTO() {
        // Required by frameworks and serializers.
    }

    public List<Long> getPermissionIds() {
        return permissionIds;
    }
    public void setPermissionIds(List<Long> permissionIds) {
        this.permissionIds = permissionIds;
    }
}
