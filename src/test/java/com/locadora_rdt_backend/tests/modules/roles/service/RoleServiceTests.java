package com.locadora_rdt_backend.tests.modules.roles.service;

import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.permissions.model.Permission;
import com.locadora_rdt_backend.modules.permissions.service.PermissionService;
import com.locadora_rdt_backend.modules.roles.dto.RoleDTO;
import com.locadora_rdt_backend.modules.roles.dto.RoleDetailsDTO;
import com.locadora_rdt_backend.modules.roles.dto.RoleInsertDTO;
import com.locadora_rdt_backend.modules.roles.dto.RolePermissionsUpdateDTO;
import com.locadora_rdt_backend.modules.roles.mapper.RoleMapper;
import com.locadora_rdt_backend.modules.roles.model.Role;
import com.locadora_rdt_backend.modules.roles.repository.RoleRepository;
import com.locadora_rdt_backend.modules.roles.service.RoleServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTests {

    @InjectMocks
    private RoleServiceImpl service;

    @Mock
    private RoleRepository repository;

    @Mock
    private PermissionService permissionService;

    @Mock
    private RoleMapper mapper;

    @Mock
    private AuthenticationFacade authenticationFacade;

    private Role role;
    private RoleDTO roleDTO;
    private RoleDetailsDTO detailsDTO;

    @BeforeEach
    void setUp() {
        role = new Role(1L, "ROLE_ADMIN", "admin", "admin");
        roleDTO = new RoleDTO();
        roleDTO.setId(1L);
        roleDTO.setAuthority("ROLE_ADMIN");
        detailsDTO = new RoleDetailsDTO();
        detailsDTO.setId(1L);
        detailsDTO.setAuthority("ROLE_ADMIN");
    }

    @Test
    void findAllPagedShouldReturnPageWithPermissionCount() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        PageImpl<Role> page = new PageImpl<>(List.of(role));

        Mockito.when(repository.findByAuthorityLikeIgnoreCase("ADMIN", pageRequest)).thenReturn(page);
        Mockito.when(repository.countPermissionsByRoleIds(List.of(1L))).thenReturn(List.<Object[]>of(new Object[]{1L, 2L}));
        Mockito.when(mapper.toDTO(role, 2L)).thenReturn(roleDTO);

        Page<RoleDTO> result = service.findAllPaged(" ADMIN ", pageRequest);

        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    void findAllPagedShouldReturnEmptyPageWhenNoRoles() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        PageImpl<Role> page = new PageImpl<>(List.of());

        Mockito.when(repository.findByAuthorityLikeIgnoreCase("", pageRequest)).thenReturn(page);

        Assertions.assertTrue(service.findAllPaged(null, pageRequest).isEmpty());
    }

    @Test
    void findByIdShouldReturnDTOWhenIdExists() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(role));
        Mockito.when(mapper.toDetailsDTO(role)).thenReturn(detailsDTO);

        Assertions.assertEquals(1L, service.findById(1L).getId());
    }

    @Test
    void findByIdShouldThrowWhenIdDoesNotExist() {
        Mockito.when(repository.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(999L));
    }

    @Test
    void updateRolePermissionsShouldReplacePermissions() {
        RolePermissionsUpdateDTO dto = new RolePermissionsUpdateDTO();
        dto.setPermissionIds(List.of(1L));
        Permission permission = new Permission(1L, "READ_USERS", "Users");

        Mockito.when(repository.getOne(1L)).thenReturn(role);
        Mockito.when(permissionService.findEntityById(1L)).thenReturn(permission);
        Mockito.when(repository.save(role)).thenReturn(role);
        Mockito.when(mapper.toDTO(role)).thenReturn(roleDTO);

        RoleDTO result = service.updateRolePermissions(1L, dto);

        Assertions.assertEquals(1L, result.getId());
        Assertions.assertTrue(role.getPermissions().contains(permission));
    }

    @Test
    void updateRolePermissionsShouldThrowWhenRoleDoesNotExist() {
        RolePermissionsUpdateDTO dto = new RolePermissionsUpdateDTO();
        dto.setPermissionIds(List.of(1L));

        Mockito.when(repository.getOne(999L)).thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.updateRolePermissions(999L, dto));
    }

    @Test
    void updateRolePermissionsShouldThrowWhenDatabaseFails() {
        RolePermissionsUpdateDTO dto = new RolePermissionsUpdateDTO();
        dto.setPermissionIds(List.of(1L));

        Mockito.when(repository.getOne(1L)).thenReturn(role);
        Mockito.when(permissionService.findEntityById(1L)).thenThrow(new DataAccessResourceFailureException("fail"));

        Assertions.assertThrows(DatabaseException.class, () -> service.updateRolePermissions(1L, dto));
    }

    @Test
    void insertShouldReturnDTOAndSetCreatedBy() {
        RoleInsertDTO dto = new RoleInsertDTO();
        dto.setAuthority("ROLE_ADMIN");

        Mockito.when(mapper.toEntity(dto)).thenReturn(role);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(role)).thenReturn(role);
        Mockito.when(mapper.toDTO(role)).thenReturn(roleDTO);

        RoleDTO result = service.insert(dto);

        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals("admin", role.getCreatedBy());
    }

    @Test
    void findEntityByIdShouldReturnEntityWhenIdExists() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(role));

        Assertions.assertEquals(role, service.findEntityById(1L));
    }
}
