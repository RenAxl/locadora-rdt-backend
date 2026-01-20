package com.locadora_rdt_backend.services;

import com.locadora_rdt_backend.dto.RoleDTO;
import com.locadora_rdt_backend.dto.RolePermissionsUpdateDTO;
import com.locadora_rdt_backend.entities.Permission;
import com.locadora_rdt_backend.entities.Role;
import com.locadora_rdt_backend.repositories.PermissionRepository;
import com.locadora_rdt_backend.repositories.RoleRepository;
import com.locadora_rdt_backend.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
    public List<RoleDTO> findAll() {
        return roleRepository.findAll()
                .stream()
                .map(RoleDTO::new)
                .collect(Collectors.toList());
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

    @Transactional
    public RoleDTO update(Long id, RoleDTO dto) {
        try {
            Role entity = roleRepository.getOne(id);
            entity.setAuthority(dto.getAuthority());
            entity = roleRepository.save(entity);
            return new RoleDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Role not found: " + id);
        }
    }

    public void delete(Long id) {
        try {
            roleRepository.deleteById(id);
        } catch (DataAccessException e) {
            throw new ResourceNotFoundException("Role not found: " + id);
        }
    }
}
