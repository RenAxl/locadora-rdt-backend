package com.locadora_rdt_backend.tests.factory;

import com.locadora_rdt_backend.dto.PermissionDTO;
import com.locadora_rdt_backend.entities.Permission;

public class PermissionFactory {

    public static Permission createPermission() {
        Permission p = new Permission();
        p.setId(1L);
        p.setName("USER_READ");
        p.setGroupName("USERS");
        return p;
    }

    public static Permission createPermission(Long id, String name, String groupName) {
        Permission p = new Permission();
        p.setId(id);
        p.setName(name);
        p.setGroupName(groupName);
        return p;
    }

    public static PermissionDTO createPermissionDTO() {
        return new PermissionDTO(createPermission());
    }
}
