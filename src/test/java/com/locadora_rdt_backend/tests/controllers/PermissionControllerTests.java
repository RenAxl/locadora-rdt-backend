package com.locadora_rdt_backend.tests.controllers;

import com.locadora_rdt_backend.controllers.PermissionController;
import com.locadora_rdt_backend.dto.PermissionDTO;
import com.locadora_rdt_backend.services.PermissionService;
import com.locadora_rdt_backend.tests.factory.PermissionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
public class PermissionControllerTests {

    @InjectMocks
    private PermissionController controller;

    @Mock
    private PermissionService service;

    private PermissionDTO permissionDTO;

    @BeforeEach
    void setUp() throws Exception {
        permissionDTO = PermissionFactory.createPermissionDTO();

        Mockito.when(service.findAll(ArgumentMatchers.anyString()))
                .thenReturn(List.of(permissionDTO));

        Mockito.when(service.findAllGroupNames())
                .thenReturn(List.of("USERS", "ROLES"));
    }


    @Test
    public void findAllShouldReturnResponseEntityWithListWhenServiceReturnsData() {

        String groupName = "USERS";

        ResponseEntity<List<PermissionDTO>> response = controller.findAll(groupName);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertFalse(response.getBody().isEmpty());
        Assertions.assertEquals(permissionDTO.getId(), response.getBody().get(0).getId());

        Mockito.verify(service, Mockito.times(1)).findAll(groupName);
    }

    @Test
    public void findAllShouldReturnResponseEntityWithEmptyListWhenServiceReturnsEmpty() {

        String groupName = "USERS";

        Mockito.when(service.findAll(groupName)).thenReturn(List.of());

        ResponseEntity<List<PermissionDTO>> response = controller.findAll(groupName);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertTrue(response.getBody().isEmpty());

        Mockito.verify(service, Mockito.times(1)).findAll(groupName);
    }

    @Test
    public void findAllShouldCallServiceWithCorrectArgumentEvenWithSpaces() {

        String groupName = "  users  ";

        controller.findAll(groupName);

        // Controller não faz trim; quem faz é o service.
        Mockito.verify(service, Mockito.times(1)).findAll(groupName);
    }

    @Test
    public void findAllShouldThrowRuntimeExceptionWhenServiceThrowsRuntimeException() {

        String groupName = "USERS";

        Mockito.when(service.findAll(groupName))
                .thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = Assertions.assertThrows(
                RuntimeException.class,
                () -> controller.findAll(groupName)
        );

        Assertions.assertEquals("DB error", ex.getMessage());
        Mockito.verify(service, Mockito.times(1)).findAll(groupName);
    }

    @Test
    public void findAllGroupsShouldReturnResponseEntityWithListWhenServiceReturnsData() {

        ResponseEntity<List<String>> response = controller.findAllGroups();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertFalse(response.getBody().isEmpty());
        Assertions.assertEquals(List.of("USERS", "ROLES"), response.getBody());

        Mockito.verify(service, Mockito.times(1)).findAllGroupNames();
    }

    @Test
    public void findAllGroupsShouldReturnResponseEntityWithEmptyListWhenServiceReturnsEmpty() {

        Mockito.when(service.findAllGroupNames()).thenReturn(List.of());

        ResponseEntity<List<String>> response = controller.findAllGroups();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertTrue(response.getBody().isEmpty());

        Mockito.verify(service, Mockito.times(1)).findAllGroupNames();
    }

    @Test
    public void findAllGroupsShouldThrowRuntimeExceptionWhenServiceThrowsRuntimeException() {

        Mockito.when(service.findAllGroupNames())
                .thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = Assertions.assertThrows(
                RuntimeException.class,
                () -> controller.findAllGroups()
        );

        Assertions.assertEquals("DB error", ex.getMessage());
        Mockito.verify(service, Mockito.times(1)).findAllGroupNames();
    }
}
