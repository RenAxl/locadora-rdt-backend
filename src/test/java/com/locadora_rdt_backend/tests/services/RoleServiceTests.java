package com.locadora_rdt_backend.tests.services;

import java.util.List;
import java.util.Optional;

import com.locadora_rdt_backend.dto.RoleDTO;
import com.locadora_rdt_backend.dto.RoleListDTO;
import com.locadora_rdt_backend.dto.RolePermissionsUpdateDTO;
import com.locadora_rdt_backend.entities.Permission;
import com.locadora_rdt_backend.entities.Role;
import com.locadora_rdt_backend.repositories.PermissionRepository;
import com.locadora_rdt_backend.repositories.RoleRepository;
import com.locadora_rdt_backend.services.RoleService;
import com.locadora_rdt_backend.services.exceptions.ResourceNotFoundException;
import com.locadora_rdt_backend.tests.factory.RoleFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class RoleServiceTests {

    @InjectMocks
    private RoleService service;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    private Role role;

    @BeforeEach
    void setUp() throws Exception {
        role = RoleFactory.createRole();

        Mockito.when(roleRepository.save(ArgumentMatchers.any(Role.class)))
                .thenReturn(role);
    }

    @Test
    public void findAllShouldReturnListOfRoleListDTO() {

        RoleListDTO dto1 = RoleFactory.createRoleListDTO(1L, "ROLE_ADMIN", 2L);
        RoleListDTO dto2 = RoleFactory.createRoleListDTO(2L, "ROLE_GERENTE", 0L);

        Mockito.when(roleRepository.findAllWithPermissionsCount())
                .thenReturn(List.of(dto1, dto2));

        List<RoleListDTO> result = service.findAll();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("ROLE_ADMIN", result.get(0).getAuthority());
        Assertions.assertEquals("ROLE_GERENTE", result.get(1).getAuthority());
        Assertions.assertEquals(2L, result.get(0).getPermissionsCount());
        Assertions.assertEquals(0L, result.get(1).getPermissionsCount());

        Mockito.verify(roleRepository, Mockito.times(1)).findAllWithPermissionsCount();
        Mockito.verify(roleRepository, Mockito.never()).findAll();
    }

    @Test
    public void findAllShouldReturnEmptyListWhenNoRoles() {

        Mockito.when(roleRepository.findAllWithPermissionsCount())
                .thenReturn(List.of());

        List<RoleListDTO> result = service.findAll();

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());

        Mockito.verify(roleRepository, Mockito.times(1)).findAllWithPermissionsCount();
        Mockito.verify(roleRepository, Mockito.never()).findAll();
    }


    @Test
    public void findByIdShouldReturnRoleDTOWhenIdExists() {

        Long existingId = 1L;
        Mockito.when(roleRepository.findById(existingId)).thenReturn(Optional.of(role));

        RoleDTO result = service.findById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.getId());
        Assertions.assertEquals(role.getAuthority(), result.getAuthority());

        Mockito.verify(roleRepository, Mockito.times(1)).findById(existingId);
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Long nonExistingId = 999L;
        Mockito.when(roleRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.findById(nonExistingId)
        );

        Assertions.assertTrue(ex.getMessage().contains("Role not found: " + nonExistingId));
        Mockito.verify(roleRepository, Mockito.times(1)).findById(nonExistingId);
    }

    @Test
    public void insertShouldReturnRoleDTOAndSaveEntity() {

        RoleDTO dto = RoleFactory.createRoleDTO("ROLE_FINANCEIRO");

        RoleDTO result = service.insert(dto);

        Assertions.assertNotNull(result);

        ArgumentCaptor<Role> captor = ArgumentCaptor.forClass(Role.class);
        Mockito.verify(roleRepository, Mockito.times(1)).save(captor.capture());

        Role saved = captor.getValue();
        Assertions.assertEquals("ROLE_FINANCEIRO", saved.getAuthority());
    }

    @Test
    public void insertShouldThrowRuntimeExceptionWhenRepositoryThrowsDataAccessException() {

        RoleDTO dto = RoleFactory.createRoleDTO("ROLE_TESTE");

        DataIntegrityViolationException dbException =
                new DataIntegrityViolationException("DB error");

        Mockito.when(roleRepository.save(ArgumentMatchers.any(Role.class)))
                .thenThrow(dbException);

        RuntimeException ex = Assertions.assertThrows(
                RuntimeException.class,
                () -> service.insert(dto)
        );

        // como não tem catch no insert(), sobe como está
        Assertions.assertEquals(dbException, ex.getCause() == null ? ex : ex.getCause());
        Mockito.verify(roleRepository, Mockito.times(1)).save(ArgumentMatchers.any(Role.class));
    }

    @Test
    public void updateShouldReturnRoleDTOWhenIdExists() {

        Long existingId = 1L;
        RoleDTO dto = RoleFactory.createRoleDTO("ROLE_ATENDENTE");

        Mockito.when(roleRepository.getOne(existingId)).thenReturn(role);
        Mockito.when(roleRepository.save(ArgumentMatchers.any(Role.class))).thenReturn(role);

        RoleDTO result = service.update(existingId, dto);

        Assertions.assertNotNull(result);

        Mockito.verify(roleRepository, Mockito.times(1)).getOne(existingId);
        Mockito.verify(roleRepository, Mockito.times(1)).save(ArgumentMatchers.any(Role.class));
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Long nonExistingId = 999L;
        RoleDTO dto = RoleFactory.createRoleDTO("ROLE_X");

        Mockito.when(roleRepository.getOne(nonExistingId))
                .thenThrow(new javax.persistence.EntityNotFoundException());

        ResourceNotFoundException ex = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.update(nonExistingId, dto)
        );

        Assertions.assertTrue(ex.getMessage().contains("Role not found: " + nonExistingId));
        Mockito.verify(roleRepository, Mockito.times(1)).getOne(nonExistingId);
        Mockito.verify(roleRepository, Mockito.never()).save(ArgumentMatchers.any(Role.class));
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {

        Long existingId = 1L;
        Mockito.doNothing().when(roleRepository).deleteById(existingId);

        Assertions.assertDoesNotThrow(() -> service.delete(existingId));

        Mockito.verify(roleRepository, Mockito.times(1)).deleteById(existingId);
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenDataAccessExceptionOccurs() {

        Long nonExistingId = 999L;

        Mockito.doThrow(new EmptyResultDataAccessException(1))
                .when(roleRepository).deleteById(nonExistingId);

        ResourceNotFoundException ex = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.delete(nonExistingId)
        );

        Assertions.assertTrue(ex.getMessage().contains("Role not found: " + nonExistingId));
        Mockito.verify(roleRepository, Mockito.times(1)).deleteById(nonExistingId);
    }

    @Test
    public void updateRolePermissionsShouldUpdatePermissionsAndReturnRoleDTOWhenAllOk() {

        Long roleId = 1L;
        List<Long> permIds = List.of(10L, 20L, 30L);
        RolePermissionsUpdateDTO dto = RoleFactory.createRolePermissionsUpdateDTO(permIds);

        Permission p1 = RoleFactory.createPermission(10L, "USER_READ");
        Permission p2 = RoleFactory.createPermission(20L, "USER_WRITE");
        Permission p3 = RoleFactory.createPermission(30L, "USER_DELETE");

        Mockito.when(roleRepository.getOne(roleId)).thenReturn(role);
        Mockito.when(permissionRepository.findById(10L)).thenReturn(Optional.of(p1));
        Mockito.when(permissionRepository.findById(20L)).thenReturn(Optional.of(p2));
        Mockito.when(permissionRepository.findById(30L)).thenReturn(Optional.of(p3));
        Mockito.when(roleRepository.save(ArgumentMatchers.any(Role.class))).thenReturn(role);

        RoleDTO result = service.updateRolePermissions(roleId, dto);

        Assertions.assertNotNull(result);

        Mockito.verify(roleRepository, Mockito.times(1)).getOne(roleId);
        Mockito.verify(permissionRepository, Mockito.times(1)).findById(10L);
        Mockito.verify(permissionRepository, Mockito.times(1)).findById(20L);
        Mockito.verify(permissionRepository, Mockito.times(1)).findById(30L);
        Mockito.verify(roleRepository, Mockito.times(1)).save(ArgumentMatchers.any(Role.class));
    }

    @Test
    public void updateRolePermissionsShouldThrowResourceNotFoundExceptionWhenRoleDoesNotExist() {

        Long nonExistingRoleId = 999L;
        RolePermissionsUpdateDTO dto = RoleFactory.createRolePermissionsUpdateDTO(List.of(10L));

        Mockito.when(roleRepository.getOne(nonExistingRoleId))
                .thenThrow(new javax.persistence.EntityNotFoundException());

        ResourceNotFoundException ex = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.updateRolePermissions(nonExistingRoleId, dto)
        );

        Assertions.assertTrue(ex.getMessage().contains("Role not found: " + nonExistingRoleId));
        Mockito.verify(roleRepository, Mockito.times(1)).getOne(nonExistingRoleId);
        Mockito.verify(roleRepository, Mockito.never()).save(ArgumentMatchers.any(Role.class));
        Mockito.verify(permissionRepository, Mockito.never()).findById(ArgumentMatchers.anyLong());
    }

    @Test
    public void updateRolePermissionsShouldThrowResourceNotFoundExceptionWhenAnyPermissionDoesNotExist() {

        Long roleId = 1L;
        RolePermissionsUpdateDTO dto = RoleFactory.createRolePermissionsUpdateDTO(List.of(10L, 999L));

        Permission p1 = RoleFactory.createPermission(10L, "USER_READ");

        Mockito.when(roleRepository.getOne(roleId)).thenReturn(role);
        Mockito.when(permissionRepository.findById(10L)).thenReturn(Optional.of(p1));
        Mockito.when(permissionRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.updateRolePermissions(roleId, dto)
        );

        Assertions.assertTrue(ex.getMessage().contains("Permission not found: 999"));

        Mockito.verify(roleRepository, Mockito.times(1)).getOne(roleId);
        Mockito.verify(permissionRepository, Mockito.times(1)).findById(10L);
        Mockito.verify(permissionRepository, Mockito.times(1)).findById(999L);
        Mockito.verify(roleRepository, Mockito.never()).save(ArgumentMatchers.any(Role.class));
    }

    @Test
    public void updateRolePermissionsShouldThrowRuntimeExceptionWhenRepositoryThrowsDataAccessException() {

        Long roleId = 1L;
        RolePermissionsUpdateDTO dto = RoleFactory.createRolePermissionsUpdateDTO(List.of(10L));

        Permission p1 = RoleFactory.createPermission(10L, "USER_READ");

        DataIntegrityViolationException dbException =
                new DataIntegrityViolationException("DB error");

        Mockito.when(roleRepository.getOne(roleId)).thenReturn(role);
        Mockito.when(permissionRepository.findById(10L)).thenReturn(Optional.of(p1));
        Mockito.when(roleRepository.save(ArgumentMatchers.any(Role.class)))
                .thenThrow(dbException);

        RuntimeException ex = Assertions.assertThrows(
                RuntimeException.class,
                () -> service.updateRolePermissions(roleId, dto)
        );

        Assertions.assertEquals("Error updating role permissions.", ex.getMessage());
        Assertions.assertEquals(dbException, ex.getCause());

        Mockito.verify(roleRepository, Mockito.times(1)).getOne(roleId);
        Mockito.verify(permissionRepository, Mockito.times(1)).findById(10L);
        Mockito.verify(roleRepository, Mockito.times(1)).save(ArgumentMatchers.any(Role.class));
    }

    @Test
    public void updateRolePermissionsShouldCallRepositoriesWithCorrectArguments() {

        Long roleId = 1L;
        List<Long> permIds = List.of(10L, 20L);
        RolePermissionsUpdateDTO dto = RoleFactory.createRolePermissionsUpdateDTO(permIds);

        Permission p1 = RoleFactory.createPermission(10L, "USER_READ");
        Permission p2 = RoleFactory.createPermission(20L, "USER_WRITE");

        Mockito.when(roleRepository.getOne(roleId)).thenReturn(role);
        Mockito.when(permissionRepository.findById(10L)).thenReturn(Optional.of(p1));
        Mockito.when(permissionRepository.findById(20L)).thenReturn(Optional.of(p2));
        Mockito.when(roleRepository.save(ArgumentMatchers.any(Role.class))).thenReturn(role);

        service.updateRolePermissions(roleId, dto);

        ArgumentCaptor<Long> roleIdCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(roleRepository).getOne(roleIdCaptor.capture());
        Assertions.assertEquals(roleId, roleIdCaptor.getValue());

        Mockito.verify(permissionRepository).findById(10L);
        Mockito.verify(permissionRepository).findById(20L);
        Mockito.verify(roleRepository).save(ArgumentMatchers.any(Role.class));
    }
}
