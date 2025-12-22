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

        // simula request atual para o ServletUriComponentsBuilder funcionar
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
}
