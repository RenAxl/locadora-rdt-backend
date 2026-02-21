package com.locadora_rdt_backend.tests.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.locadora_rdt_backend.controllers.UserController;
import com.locadora_rdt_backend.dto.UserDTO;
import com.locadora_rdt_backend.dto.UserInsertDTO;
import com.locadora_rdt_backend.services.UserService;
import com.locadora_rdt_backend.tests.factory.UserFactory;

import java.net.URI;
import java.util.List;

@ExtendWith(SpringExtension.class)
public class UserControllerTests {

    @InjectMocks
    private UserController controller;

    @Mock
    private UserService service;

    private UserDTO userDTO;
    private PageImpl<UserDTO> page;

    @BeforeEach
    void setUp() throws Exception {

        userDTO = UserFactory.createUserDTO();
        page = new PageImpl<>(List.of(userDTO));

        Mockito.when(service.findAllPaged(ArgumentMatchers.anyString(), ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(page);

        Mockito.when(service.insert(ArgumentMatchers.any(UserInsertDTO.class)))
                .thenReturn(userDTO);
    }

    @Test
    public void findAllPagedShouldReturnResponseEntityWithPage() {

        String name = " Renan "; // com espaços pra validar o trim()
        Integer pageNumber = 0;
        Integer linesPerPage = 3;
        String direction = "ASC";
        String orderBy = "name";

        ResponseEntity<Page<UserDTO>> response = controller.findAllPaged(
                name, pageNumber, linesPerPage, direction, orderBy
        );

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertFalse(response.getBody().isEmpty());

        PageRequest expectedPageRequest = PageRequest.of(pageNumber, linesPerPage, Sort.Direction.valueOf(direction), orderBy);
        Mockito.verify(service, Mockito.times(1)).findAllPaged(name.trim(), expectedPageRequest);
    }

    @Test
    public void insertShouldReturnCreatedAndLocationHeader() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/users");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        UserInsertDTO dto = UserFactory.createUserInsertDTO();

        ResponseEntity<UserDTO> response = controller.insert(dto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(201, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(userDTO.getId(), response.getBody().getId());

        URI location = response.getHeaders().getLocation();
        Assertions.assertNotNull(location);
        Assertions.assertTrue(location.toString().endsWith("/users/" + userDTO.getId()));

        Mockito.verify(service, Mockito.times(1)).insert(dto);
    }

    @Test
    public void findByIdShouldReturnResponseEntityWithUserDTOWhenIdExists() {

        Long existingId = 1L;

        Mockito.when(service.findById(existingId)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = controller.findById(existingId);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(userDTO.getId(), response.getBody().getId());

        Mockito.verify(service, Mockito.times(1)).findById(existingId);
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Long nonExistingId = 1000L;

        Mockito.when(service.findById(nonExistingId))
                .thenThrow(new com.locadora_rdt_backend.services.exceptions.ResourceNotFoundException("Entity not found"));

        Assertions.assertThrows(com.locadora_rdt_backend.services.exceptions.ResourceNotFoundException.class, () -> {
            controller.findById(nonExistingId);
        });

        Mockito.verify(service, Mockito.times(1)).findById(nonExistingId);
    }

    @Test
    public void updateShouldReturnResponseEntityWithUserDTOWhenIdExists() {

        Long existingId = 1L;

        com.locadora_rdt_backend.dto.UserUpdateDTO dto = new com.locadora_rdt_backend.dto.UserUpdateDTO();

        Mockito.when(service.update(existingId, dto)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = controller.update(existingId, dto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(userDTO.getId(), response.getBody().getId());

        Mockito.verify(service, Mockito.times(1)).update(existingId, dto);
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Long nonExistingId = 1000L;

        com.locadora_rdt_backend.dto.UserUpdateDTO dto = new com.locadora_rdt_backend.dto.UserUpdateDTO();

        Mockito.when(service.update(nonExistingId, dto))
                .thenThrow(new com.locadora_rdt_backend.services.exceptions.ResourceNotFoundException("Id not found " + nonExistingId));

        Assertions.assertThrows(com.locadora_rdt_backend.services.exceptions.ResourceNotFoundException.class, () -> {
            controller.update(nonExistingId, dto);
        });

        Mockito.verify(service, Mockito.times(1)).update(nonExistingId, dto);
    }

    @Test
    public void deleteShouldReturnNoContentWhenIdExists() {

        Long existingId = 1L;

        Mockito.doNothing().when(service).delete(existingId);

        ResponseEntity<UserDTO> response = controller.delete(existingId);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(204, response.getStatusCodeValue());
        Assertions.assertNull(response.getBody());

        Mockito.verify(service, Mockito.times(1)).delete(existingId);
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Long nonExistingId = 1000L;

        Mockito.doThrow(new com.locadora_rdt_backend.services.exceptions.ResourceNotFoundException("Id not found " + nonExistingId))
                .when(service).delete(nonExistingId);

        Assertions.assertThrows(com.locadora_rdt_backend.services.exceptions.ResourceNotFoundException.class, () -> {
            controller.delete(nonExistingId);
        });

        Mockito.verify(service, Mockito.times(1)).delete(nonExistingId);
    }

    @Test
    public void deleteAllShouldReturnNoContentAndCallServiceDeleteAll() {

        List<Long> ids = List.of(1L, 2L, 3L);

        Mockito.doNothing().when(service).deleteAll(ids);

        ResponseEntity<Void> response = controller.deleteAll(ids);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(204, response.getStatusCodeValue());
        Assertions.assertNull(response.getBody());

        Mockito.verify(service, Mockito.times(1)).deleteAll(ids);
    }

    @Test
    public void deleteAllShouldThrowIllegalArgumentExceptionWhenIdsIsNull() {

        Mockito.doThrow(new IllegalArgumentException("Lista de ids vazia"))
                .when(service).deleteAll(null);

        Assertions.assertThrows(IllegalArgumentException.class, () -> controller.deleteAll(null));

        Mockito.verify(service, Mockito.times(1)).deleteAll(null);
    }

    @Test
    public void deleteAllShouldThrowIllegalArgumentExceptionWhenIdsIsEmpty() {

        List<Long> ids = List.of();

        Mockito.doThrow(new IllegalArgumentException("Lista de ids vazia"))
                .when(service).deleteAll(ids);

        Assertions.assertThrows(IllegalArgumentException.class, () -> controller.deleteAll(ids));

        Mockito.verify(service, Mockito.times(1)).deleteAll(ids);
    }

    @Test
    public void deleteAllShouldThrowResourceNotFoundExceptionWhenAnyIdDoesNotExist() {

        List<Long> ids = List.of(1L, 2L, 3L);

        Mockito.doThrow(new com.locadora_rdt_backend.services.exceptions.ResourceNotFoundException("Um ou mais IDs não existem"))
                .when(service).deleteAll(ids);

        Assertions.assertThrows(com.locadora_rdt_backend.services.exceptions.ResourceNotFoundException.class, () -> controller.deleteAll(ids));

        Mockito.verify(service, Mockito.times(1)).deleteAll(ids);
    }

    @Test
    public void changeActiveShouldReturnNoContentWhenIdExists() {

        Long existingId = 1L;
        boolean active = true;

        Mockito.doNothing().when(service).changeActiveStatus(existingId, active);

        ResponseEntity<UserDTO> response = controller.changeActive(existingId, active);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(204, response.getStatusCodeValue());
        Assertions.assertNull(response.getBody());

        Mockito.verify(service, Mockito.times(1)).changeActiveStatus(existingId, active);
    }

    @Test
    public void changeActiveShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Long nonExistingId = 1000L;
        boolean active = false;

        Mockito.doThrow(new com.locadora_rdt_backend.services.exceptions.ResourceNotFoundException("Id not found " + nonExistingId))
                .when(service).changeActiveStatus(nonExistingId, active);

        Assertions.assertThrows(
                com.locadora_rdt_backend.services.exceptions.ResourceNotFoundException.class,
                () -> controller.changeActive(nonExistingId, active)
        );

        Mockito.verify(service, Mockito.times(1)).changeActiveStatus(nonExistingId, active);
    }

    @Test
    public void changeActiveShouldThrowRuntimeExceptionWhenServiceThrowsRuntimeException() {

        Long existingId = 1L;
        boolean active = true;

        Mockito.doThrow(new RuntimeException("Error changing user status."))
                .when(service).changeActiveStatus(existingId, active);

        RuntimeException ex = Assertions.assertThrows(
                RuntimeException.class,
                () -> controller.changeActive(existingId, active)
        );

        Assertions.assertEquals("Error changing user status.", ex.getMessage());

        Mockito.verify(service, Mockito.times(1)).changeActiveStatus(existingId, active);
    }

    @Test
    public void changeActiveShouldCallServiceWithCorrectArguments() {

        Long existingId = 1L;
        boolean active = false;

        Mockito.doNothing().when(service).changeActiveStatus(ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean());

        controller.changeActive(existingId, active);

        Mockito.verify(service, Mockito.times(1)).changeActiveStatus(existingId, active);
    }

    @Test
    public void getMeShouldReturnOkAndUserDTOWhenServiceReturnsDTO() {

        Authentication authentication = Mockito.mock(Authentication.class);

        Mockito.when(service.getMe(authentication)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = controller.getMe(authentication);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(userDTO.getId(), response.getBody().getId());

        Mockito.verify(service, Mockito.times(1)).getMe(authentication);
    }

    @Test
    public void getMeShouldThrowUsernameNotFoundExceptionWhenServiceThrowsUsernameNotFoundException() {

        Authentication authentication = Mockito.mock(Authentication.class);

        Mockito.when(service.getMe(authentication))
                .thenThrow(new UsernameNotFoundException("Usuário não encontrado"));

        UsernameNotFoundException ex = Assertions.assertThrows(
                UsernameNotFoundException.class,
                () -> controller.getMe(authentication)
        );

        Assertions.assertEquals("Usuário não encontrado", ex.getMessage());

        Mockito.verify(service, Mockito.times(1)).getMe(authentication);
    }

    @Test
    public void getMeShouldThrowNullPointerExceptionWhenAuthenticationIsNullAndServiceThrowsNPE() {

        Authentication authentication = null;

        Mockito.when(service.getMe(ArgumentMatchers.isNull()))
                .thenThrow(new NullPointerException("authentication is null"));

        NullPointerException ex = Assertions.assertThrows(
                NullPointerException.class,
                () -> controller.getMe(authentication)
        );

        Assertions.assertEquals("authentication is null", ex.getMessage());

        Mockito.verify(service, Mockito.times(1)).getMe(ArgumentMatchers.isNull());
    }

    @Test
    public void getMeShouldThrowRuntimeExceptionWhenServiceThrowsRuntimeException() {

        Authentication authentication = Mockito.mock(Authentication.class);

        Mockito.when(service.getMe(authentication))
                .thenThrow(new RuntimeException("Erro inesperado"));

        RuntimeException ex = Assertions.assertThrows(
                RuntimeException.class,
                () -> controller.getMe(authentication)
        );

        Assertions.assertEquals("Erro inesperado", ex.getMessage());

        Mockito.verify(service, Mockito.times(1)).getMe(authentication);
    }

}
