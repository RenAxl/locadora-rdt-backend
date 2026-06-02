package com.locadora_rdt_backend.modules.permissions.service;

import com.locadora_rdt_backend.modules.permissions.dto.PermissionDTO;
import com.locadora_rdt_backend.modules.permissions.model.Permission;
import java.util.List;

public interface PermissionService {

    List<PermissionDTO> findAll(String groupName);

    List<String> findAllGroupNames();

    Permission findEntityById(Long id);
}
