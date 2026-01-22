package com.locadora_rdt_backend.tests.controllers;

import com.locadora_rdt_backend.controllers.RoleController;
import com.locadora_rdt_backend.dto.RoleDTO;
import com.locadora_rdt_backend.dto.RolePermissionsUpdateDTO;
import com.locadora_rdt_backend.dto.RoleListDTO;
import com.locadora_rdt_backend.services.RoleService;
import com.locadora_rdt_backend.services.exceptions.ResourceNotFoundException;
import com.locadora_rdt_backend.tests.factory.RoleFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URI;
import java.util.List;

@ExtendWith(SpringExtension.class)
public class RoleControllerTests {

    @InjectMocks
    private RoleController controller;

    @Mock
    private RoleService service;

    private RoleDTO roleDTO;

    private RoleListDTO roleListDTO;


    @BeforeEach
    void setUp() throws Exception {
        roleDTO = RoleFactory.createRoleDTO();
        roleListDTO = RoleFactory.createRoleListDTO(1L, "ROLE_ADMIN", 2L);

        Mockito.when(service.findAll())
                .thenReturn(List.of(roleListDTO));

        Mockito.when(service.insert(ArgumentMatchers.any(RoleDTO.class)))
                .thenReturn(roleDTO);
    }


    @Test
    public void findAllShouldReturnResponseEntityWithList() {

        ResponseEntity<List<RoleListDTO>> response = controller.findAll();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertFalse(response.getBody().isEmpty());

        Mockito.verify(service, Mockito.times(1)).findAll();
    }


    @Test
    public void findAllShouldReturnEmptyListWhenServiceReturnsEmpty() {

        Mockito.when(service.findAll()).thenReturn(List.of());

        ResponseEntity<List<RoleListDTO>> response = controller.findAll();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertTrue(response.getBody().isEmpty());

        Mockito.verify(service, Mockito.times(1)).findAll();
    }


    @Test
    public void findAllShouldThrowRuntimeExceptionWhenServiceThrowsRuntimeException() {

        Mockito.when(service.findAll()).thenThrow(new RuntimeException("Unexpected error"));

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () -> controller.findAll());
        Assertions.assertEquals("Unexpected error", ex.getMessage());

        Mockito.verify(service, Mockito.times(1)).findAll();
    }


    @Test
    public void findByIdShouldReturnResponseEntityWithRoleDTOWhenIdExists() {

        Long existingId = 1L;

        Mockito.when(service.findById(existingId)).thenReturn(roleDTO);

        ResponseEntity<RoleDTO> response = controller.findById(existingId);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(roleDTO.getId(), response.getBody().getId());

        Mockito.verify(service, Mockito.times(1)).findById(existingId);
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Long nonExistingId = 1000L;

        Mockito.when(service.findById(nonExistingId))
                .thenThrow(new ResourceNotFoundException("Role not found: " + nonExistingId));

        Assertions.assertThrows(ResourceNotFoundException.class, () -> controller.findById(nonExistingId));

        Mockito.verify(service, Mockito.times(1)).findById(nonExistingId);
    }

    @Test
    public void findByIdShouldThrowRuntimeExceptionWhenServiceThrowsRuntimeException() {

        Long existingId = 1L;

        Mockito.when(service.findById(existingId))
                .thenThrow(new RuntimeException("Unexpected error"));

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () -> controller.findById(existingId));
        Assertions.assertEquals("Unexpected error", ex.getMessage());

        Mockito.verify(service, Mockito.times(1)).findById(existingId);
    }

    @Test
    public void updatePermissionsShouldReturnOkWithRoleDTOWhenRoleExistsAndPermissionsOk() {

        Long roleId = 1L;
        RolePermissionsUpdateDTO dto = RoleFactory.createRolePermissionsUpdateDTO(List.of(10L, 20L));

        Mockito.when(service.updateRolePermissions(roleId, dto)).thenReturn(roleDTO);

        ResponseEntity<RoleDTO> response = controller.updatePermissions(roleId, dto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(roleDTO.getId(), response.getBody().getId());

        Mockito.verify(service, Mockito.times(1)).updateRolePermissions(roleId, dto);
    }

    @Test
    public void updatePermissionsShouldThrowResourceNotFoundExceptionWhenRoleDoesNotExist() {

        Long roleId = 999L;
        RolePermissionsUpdateDTO dto = RoleFactory.createRolePermissionsUpdateDTO(List.of(10L));

        Mockito.when(service.updateRolePermissions(roleId, dto))
                .thenThrow(new ResourceNotFoundException("Role not found: " + roleId));

        Assertions.assertThrows(ResourceNotFoundException.class, () -> controller.updatePermissions(roleId, dto));

        Mockito.verify(service, Mockito.times(1)).updateRolePermissions(roleId, dto);
    }

    @Test
    public void updatePermissionsShouldThrowResourceNotFoundExceptionWhenPermissionDoesNotExist() {

        Long roleId = 1L;
        RolePermissionsUpdateDTO dto = RoleFactory.createRolePermissionsUpdateDTO(List.of(999L));

        Mockito.when(service.updateRolePermissions(roleId, dto))
                .thenThrow(new ResourceNotFoundException("Permission not found: 999"));

        Assertions.assertThrows(ResourceNotFoundException.class, () -> controller.updatePermissions(roleId, dto));

        Mockito.verify(service, Mockito.times(1)).updateRolePermissions(roleId, dto);
    }

    @Test
    public void updatePermissionsShouldThrowRuntimeExceptionWhenServiceThrowsRuntimeException() {

        Long roleId = 1L;
        RolePermissionsUpdateDTO dto = RoleFactory.createRolePermissionsUpdateDTO(List.of(10L));

        Mockito.when(service.updateRolePermissions(roleId, dto))
                .thenThrow(new RuntimeException("Error updating role permissions."));

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () -> controller.updatePermissions(roleId, dto));
        Assertions.assertEquals("Error updating role permissions.", ex.getMessage());

        Mockito.verify(service, Mockito.times(1)).updateRolePermissions(roleId, dto);
    }

    @Test
    public void updatePermissionsShouldCallServiceWithCorrectArguments() {

        Long roleId = 1L;
        RolePermissionsUpdateDTO dto = RoleFactory.createRolePermissionsUpdateDTO(List.of(10L, 20L));

        Mockito.when(service.updateRolePermissions(ArgumentMatchers.anyLong(), ArgumentMatchers.any(RolePermissionsUpdateDTO.class)))
                .thenReturn(roleDTO);

        controller.updatePermissions(roleId, dto);

        Mockito.verify(service, Mockito.times(1)).updateRolePermissions(roleId, dto);
    }

    @Test
    public void insertShouldReturnCreatedAndLocationHeader() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/roles");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        RoleDTO dto = RoleFactory.createRoleDTO("ROLE_GERENTE");

        Mockito.when(service.insert(dto)).thenReturn(roleDTO);

        ResponseEntity<RoleDTO> response = controller.insert(dto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(201, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(roleDTO.getId(), response.getBody().getId());

        URI location = response.getHeaders().getLocation();
        Assertions.assertNotNull(location);
        Assertions.assertTrue(location.toString().endsWith("/roles/" + roleDTO.getId()));

        Mockito.verify(service, Mockito.times(1)).insert(dto);
    }

    @Test
    public void insertShouldThrowRuntimeExceptionWhenServiceThrowsRuntimeException() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/roles");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        RoleDTO dto = RoleFactory.createRoleDTO("ROLE_X");

        Mockito.when(service.insert(dto))
                .thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () -> controller.insert(dto));
        Assertions.assertEquals("DB error", ex.getMessage());

        Mockito.verify(service, Mockito.times(1)).insert(dto);
    }


    @Test
    public void updateShouldReturnOkWithRoleDTOWhenIdExists() {

        Long existingId = 1L;
        RoleDTO dto = RoleFactory.createRoleDTO("ROLE_ATENDENTE");

        Mockito.when(service.update(existingId, dto)).thenReturn(roleDTO);

        ResponseEntity<RoleDTO> response = controller.update(existingId, dto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(roleDTO.getId(), response.getBody().getId());

        Mockito.verify(service, Mockito.times(1)).update(existingId, dto);
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Long nonExistingId = 999L;
        RoleDTO dto = RoleFactory.createRoleDTO("ROLE_X");

        Mockito.when(service.update(nonExistingId, dto))
                .thenThrow(new ResourceNotFoundException("Role not found: " + nonExistingId));

        Assertions.assertThrows(ResourceNotFoundException.class, () -> controller.update(nonExistingId, dto));

        Mockito.verify(service, Mockito.times(1)).update(nonExistingId, dto);
    }

    @Test
    public void updateShouldThrowRuntimeExceptionWhenServiceThrowsRuntimeException() {

        Long existingId = 1L;
        RoleDTO dto = RoleFactory.createRoleDTO("ROLE_X");

        Mockito.when(service.update(existingId, dto))
                .thenThrow(new RuntimeException("Unexpected error"));

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () -> controller.update(existingId, dto));
        Assertions.assertEquals("Unexpected error", ex.getMessage());

        Mockito.verify(service, Mockito.times(1)).update(existingId, dto);
    }

    @Test
    public void deleteShouldReturnNoContentWhenIdExists() {

        Long existingId = 1L;

        Mockito.doNothing().when(service).delete(existingId);

        ResponseEntity<Void> response = controller.delete(existingId);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(204, response.getStatusCodeValue());
        Assertions.assertNull(response.getBody());

        Mockito.verify(service, Mockito.times(1)).delete(existingId);
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Long nonExistingId = 999L;

        Mockito.doThrow(new ResourceNotFoundException("Role not found: " + nonExistingId))
                .when(service).delete(nonExistingId);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> controller.delete(nonExistingId));

        Mockito.verify(service, Mockito.times(1)).delete(nonExistingId);
    }

    @Test
    public void deleteShouldThrowRuntimeExceptionWhenServiceThrowsRuntimeException() {

        Long existingId = 1L;

        Mockito.doThrow(new RuntimeException("Unexpected error"))
                .when(service).delete(existingId);

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () -> controller.delete(existingId));
        Assertions.assertEquals("Unexpected error", ex.getMessage());

        Mockito.verify(service, Mockito.times(1)).delete(existingId);
    }

    @Test
    public void deleteShouldCallServiceWithCorrectArgument() {

        Long existingId = 1L;

        Mockito.doNothing().when(service).delete(ArgumentMatchers.anyLong());

        controller.delete(existingId);

        Mockito.verify(service, Mockito.times(1)).delete(existingId);
    }
}
