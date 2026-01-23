package com.locadora_rdt_backend.services;

import com.locadora_rdt_backend.dto.RoleDTO;
import com.locadora_rdt_backend.dto.RoleListDTO;
import com.locadora_rdt_backend.dto.RolePermissionsUpdateDTO;
import com.locadora_rdt_backend.entities.Permission;
import com.locadora_rdt_backend.entities.Role;
import com.locadora_rdt_backend.repositories.PermissionRepository;
import com.locadora_rdt_backend.repositories.RoleRepository;
import com.locadora_rdt_backend.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public Page<RoleListDTO> findAllPaged(String authority, PageRequest pageRequest) {

        Page<Role> page = roleRepository.findByAuthorityLikeIgnoreCase(authority, pageRequest);

        List<Long> roleIds = page.getContent()
                .stream()
                .map(Role::getId)
                .collect(Collectors.toList());

        if (roleIds.isEmpty()) {
            return page.map(r -> new RoleListDTO(r.getId(), r.getAuthority(), 0L));
        }

        List<Object[]> rows = roleRepository.countPermissionsByRoleIds(roleIds);

        var countMap = rows.stream().collect(Collectors.toMap(
                r -> (Long) r[0],
                r -> (Long) r[1]
        ));

        return page.map(r -> new RoleListDTO(
                r.getId(),
                r.getAuthority(),
                countMap.getOrDefault(r.getId(), 0L)
        ));
    }


    @Transactional(readOnly = true)
    public RoleDTO findById(Long id) {
        Optional<Role> obj = roleRepository.findById(id);
        Role entity = obj.orElseThrow(() -> new ResourceNotFoundException("Role not found: " + id));
        return new RoleDTO(entity);
    }

    @Transactional
    public RoleDTO updateRolePermissions(Long roleId, RolePermissionsUpdateDTO dto) {
        try {
            Role role = roleRepository.getOne(roleId);

            role.getPermissions().clear();

            for (Long permissionId : dto.getPermissionIds()) {
                Permission p = permissionRepository.findById(permissionId)
                        .orElseThrow(() -> new ResourceNotFoundException("Permission not found: " + permissionId));
                role.getPermissions().add(p);
            }

            role = roleRepository.save(role);
            return new RoleDTO(role);

        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Role not found: " + roleId);
        } catch (DataAccessException e) {
            throw new RuntimeException("Error updating role permissions.", e);
        }
    }

    @Transactional
    public RoleDTO insert(RoleDTO dto) {
        Role entity = new Role();
        entity.setAuthority(dto.getAuthority());
        entity = roleRepository.save(entity);
        return new RoleDTO(entity);
    }
}
