package com.locadora_rdt_backend.modules.roles.service;

import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.modules.roles.dto.RoleDTO;
import com.locadora_rdt_backend.modules.roles.dto.RoleDetailsDTO;
import com.locadora_rdt_backend.modules.roles.dto.RoleInsertDTO;
import com.locadora_rdt_backend.modules.roles.dto.RolePermissionsUpdateDTO;
import com.locadora_rdt_backend.modules.permissions.model.Permission;
import com.locadora_rdt_backend.modules.roles.mapper.RoleMapper;
import com.locadora_rdt_backend.modules.roles.model.Role;
import com.locadora_rdt_backend.modules.permissions.repository.PermissionRepository;
import com.locadora_rdt_backend.modules.roles.repository.RoleRepository;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository repository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper mapper;

    public RoleService (
            RoleRepository repository,
            PermissionRepository permissionRepository,
            RoleMapper mapper
    ) {
        this.repository = repository;
        this.permissionRepository = permissionRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public Page<RoleDTO> findAllPaged(String authority, PageRequest pageRequest) {

        String search = authority == null ? "" : authority.trim();

        Page<Role> page = repository.findByAuthorityLikeIgnoreCase(search, pageRequest);

        List<Long> roleIds = page.getContent()
                .stream()
                .map(Role::getId)
                .collect(Collectors.toList());

        if (roleIds.isEmpty()) {
            return page.map(role -> mapper.toDTO(role, 0L));
        }

        List<Object[]> rows = repository.countPermissionsByRoleIds(roleIds);

        Map<Long, Long> permissionsCountMap = rows.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));

        return page.map(role -> mapper.toDTO(
                role,
                permissionsCountMap.getOrDefault(role.getId(), 0L)
        ));
    }


    @Transactional(readOnly = true)
    public RoleDetailsDTO findById(Long id) {
        Role entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil não encontrado"));

        return mapper.toDetailsDTO(entity);
    }

    @Transactional
    public RoleDTO updateRolePermissions(Long roleId, RolePermissionsUpdateDTO dto) {

        try {

            Role role = repository.getOne(roleId);

            role.getPermissions().clear();

            for (Long permissionId : dto.getPermissionIds()) {

                Permission permission = permissionRepository.findById(permissionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Permission not found: " + permissionId
                                )
                        );

                role.getPermissions().add(permission);
            }

            role = repository.save(role);

            return mapper.toDTO(role);

        } catch (EntityNotFoundException e) {

            throw new ResourceNotFoundException(
                    "Role not found: " + roleId
            );

        } catch (DataAccessException e) {

            throw new DatabaseException(
                    "Error updating role permissions."
            );
        }
    }

    @Transactional
    public RoleDTO insert(RoleInsertDTO dto) {
        Role entity = mapper.toEntity(dto);
        entity.setCreatedBy(getAuthenticatedUsername());
        entity = repository.save(entity);
        return mapper.toDTO(entity);
    }

    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return "SYSTEM";
        }

        return authentication.getName();
    }
}
