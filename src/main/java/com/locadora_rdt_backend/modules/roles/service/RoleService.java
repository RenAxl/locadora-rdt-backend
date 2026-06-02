package com.locadora_rdt_backend.modules.roles.service;

import com.locadora_rdt_backend.modules.roles.dto.RoleDTO;
import com.locadora_rdt_backend.modules.roles.dto.RoleDetailsDTO;
import com.locadora_rdt_backend.modules.roles.dto.RoleInsertDTO;
import com.locadora_rdt_backend.modules.roles.dto.RolePermissionsUpdateDTO;
import com.locadora_rdt_backend.modules.roles.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface RoleService {

    Page<RoleDTO> findAllPaged(String authority, PageRequest pageRequest);

    RoleDetailsDTO findById(Long id);

    RoleDTO updateRolePermissions(Long roleId, RolePermissionsUpdateDTO dto);

    RoleDTO insert(RoleInsertDTO dto);

    Role findEntityById(Long id);
}
