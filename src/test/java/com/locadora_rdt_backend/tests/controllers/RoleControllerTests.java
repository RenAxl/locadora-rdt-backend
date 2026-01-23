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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

        PageRequest pageRequest = PageRequest.of(0, 12, Sort.Direction.ASC, "authority");
        Page<RoleListDTO> page = new org.springframework.data.domain.PageImpl<>(List.of(roleListDTO), pageRequest, 1);

        Mockito.when(service.findAllPaged(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(PageRequest.class)
        )).thenReturn(page);


        Mockito.when(service.insert(ArgumentMatchers.any(RoleDTO.class)))
                .thenReturn(roleDTO);
    }

    @Test
    public void findAllPagedShouldReturnResponseEntityWithPage() {

        ResponseEntity<org.springframework.data.domain.Page<RoleListDTO>> response =
                controller.findAllPaged("", 0, 12, "ASC", "authority");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertFalse(response.getBody().getContent().isEmpty());

        Mockito.verify(service, Mockito.times(1))
                .findAllPaged(ArgumentMatchers.eq(""), ArgumentMatchers.any(PageRequest.class));
    }

    @Test
    public void findAllPagedShouldReturnEmptyPageWhenServiceReturnsEmpty() {

        PageRequest pr = PageRequest.of(0, 12, Sort.Direction.ASC, "authority");
        org.springframework.data.domain.Page<RoleListDTO> emptyPage =
                new org.springframework.data.domain.PageImpl<>(List.of(), pr, 0);

        Mockito.when(service.findAllPaged(
                ArgumentMatchers.eq(""),
                ArgumentMatchers.any(PageRequest.class)
        )).thenReturn(emptyPage);

        ResponseEntity<org.springframework.data.domain.Page<RoleListDTO>> response =
                controller.findAllPaged("", 0, 12, "ASC", "authority");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertTrue(response.getBody().getContent().isEmpty());
        Assertions.assertEquals(0, response.getBody().getTotalElements());

        Mockito.verify(service, Mockito.times(1))
                .findAllPaged(ArgumentMatchers.eq(""), ArgumentMatchers.any(PageRequest.class));
    }

    @Test
    public void findAllPagedShouldThrowRuntimeExceptionWhenServiceThrowsRuntimeException() {

        Mockito.when(service.findAllPaged(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(PageRequest.class)
        )).thenThrow(new RuntimeException("Unexpected error"));

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () ->
                controller.findAllPaged("", 0, 12, "ASC", "authority")
        );

        Assertions.assertEquals("Unexpected error", ex.getMessage());

        Mockito.verify(service, Mockito.times(1))
                .findAllPaged(ArgumentMatchers.eq(""), ArgumentMatchers.any(PageRequest.class));
    }

    @Test
    public void findAllPagedShouldTrimAuthority() {

        controller.findAllPaged("  ADMIN  ", 0, 12, "ASC", "authority");

        Mockito.verify(service, Mockito.times(1))
                .findAllPaged(ArgumentMatchers.eq("ADMIN"), ArgumentMatchers.any(PageRequest.class));
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
}
