package com.locadora_rdt_backend.tests.factory;

import java.util.List;

import com.locadora_rdt_backend.dto.RoleDTO;
import com.locadora_rdt_backend.dto.RoleListDTO;
import com.locadora_rdt_backend.dto.RolePermissionsUpdateDTO;
import com.locadora_rdt_backend.entities.Permission;
import com.locadora_rdt_backend.entities.Role;

public class RoleFactory {

    public static Role createRole() {
        Role role = new Role();
        role.setId(1L);
        role.setAuthority("ROLE_ADMIN");

        // Como Role NÃO tem setPermissions(), usa o getter
        role.getPermissions().clear();
        role.getPermissions().add(createPermission(10L, "USER_READ"));
        role.getPermissions().add(createPermission(20L, "USER_WRITE"));

        return role;
    }

    public static Role createRole(Long id, String authority) {
        Role role = new Role();
        role.setId(id);
        role.setAuthority(authority);

        // garante que inicia limpo
        role.getPermissions().clear();
        return role;
    }

    public static RoleDTO createRoleDTO() {
        return new RoleDTO(createRole());
    }

    public static RoleDTO createRoleDTO(String authority) {
        RoleDTO dto = new RoleDTO();
        dto.setAuthority(authority);
        return dto;
    }

    public static Permission createPermission(Long id, String name) {
        Permission p = new Permission();
        p.setId(id);
        p.setName(name);
        return p;
    }

    public static RolePermissionsUpdateDTO createRolePermissionsUpdateDTO(List<Long> ids) {
        RolePermissionsUpdateDTO dto = new RolePermissionsUpdateDTO();
        dto.setPermissionIds(ids);
        return dto;
    }

    public static RoleListDTO createRoleListDTO() {
        return new RoleListDTO(
                1L,
                "ROLE_ADMIN",
                2L
        );
    }

    public static RoleListDTO createRoleListDTO(Long id, String authority, Long permissionsCount) {
        return new RoleListDTO(
                id,
                authority,
                permissionsCount
        );
    }
}
