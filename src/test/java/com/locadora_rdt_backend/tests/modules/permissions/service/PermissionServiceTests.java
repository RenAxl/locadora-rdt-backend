package com.locadora_rdt_backend.tests.modules.permissions.service;

import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.permissions.dto.PermissionDTO;
import com.locadora_rdt_backend.modules.permissions.model.Permission;
import com.locadora_rdt_backend.modules.permissions.repository.PermissionRepository;
import com.locadora_rdt_backend.modules.permissions.service.PermissionServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTests {

    @InjectMocks
    private PermissionServiceImpl service;

    @Mock
    private PermissionRepository repository;

    private Permission permission;

    @BeforeEach
    void setUp() {
        permission = new Permission(1L, "READ_USERS", "Users");
    }

    @Test
    void findAllShouldReturnAllWhenGroupNameIsBlank() {
        Mockito.when(repository.findAllByOrderByGroupNameAscNameAsc()).thenReturn(List.of(permission));

        List<PermissionDTO> result = service.findAll(" ");

        Assertions.assertEquals(1, result.size());
        Mockito.verify(repository).findAllByOrderByGroupNameAscNameAsc();
    }

    @Test
    void findAllShouldFilterByGroupName() {
        Mockito.when(repository.findByGroupNameIgnoreCaseOrderByNameAsc("Users")).thenReturn(List.of(permission));

        List<PermissionDTO> result = service.findAll(" Users ");

        Assertions.assertEquals("READ_USERS", result.get(0).getName());
        Mockito.verify(repository).findByGroupNameIgnoreCaseOrderByNameAsc("Users");
    }

    @Test
    void findAllGroupNamesShouldReturnGroups() {
        Mockito.when(repository.findDistinctGroupNames()).thenReturn(List.of("Users"));

        Assertions.assertEquals(List.of("Users"), service.findAllGroupNames());
    }

    @Test
    void findEntityByIdShouldReturnEntityWhenIdExists() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(permission));

        Assertions.assertEquals(permission, service.findEntityById(1L));
    }

    @Test
    void findEntityByIdShouldThrowWhenIdDoesNotExist() {
        Mockito.when(repository.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findEntityById(999L));
    }
}
