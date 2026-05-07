package com.locadora_rdt_backend.tests.modules.customers.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.customers.controller.CustomerController;
import com.locadora_rdt_backend.modules.customers.dto.CustomerDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerDetailsDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerInsertDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerUpdateDTO;
import com.locadora_rdt_backend.modules.customers.model.Customer;
import com.locadora_rdt_backend.modules.customers.repository.CustomerRepository;
import com.locadora_rdt_backend.modules.customers.service.CustomerService;
import com.locadora_rdt_backend.tests.modules.customers.factory.CustomerFactory;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
public class CustomerControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService service;

    @MockBean
    private CustomerRepository repository;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    public void findAllPagedShouldReturnPagedCustomers() throws Exception {
        CustomerDTO dto = CustomerFactory.createCustomerDTO();
        Page<CustomerDTO> page = new PageImpl<>(List.of(dto));

        PageRequest expectedPageRequest =
                PageRequest.of(0, 3, Sort.Direction.ASC, "name");

        Mockito.when(service.findAllPaged(eq("Maria"), eq(expectedPageRequest)))
                .thenReturn(page);

        mockMvc.perform(get("/customers")
                        .param("name", "Maria")
                        .param("page", "0")
                        .param("linesPerPage", "3")
                        .param("direction", "ASC")
                        .param("orderBy", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].name", is("Maria Silva")))
                .andExpect(jsonPath("$.content[0].cpf", is("12345678900")))
                .andExpect(jsonPath("$.content[0].email", is("maria@email.com")))
                .andExpect(jsonPath("$.totalElements", is(1)));

        Mockito.verify(service).findAllPaged(eq("Maria"), eq(expectedPageRequest));
    }

    @Test
    public void findAllPagedShouldUseDefaultParams() throws Exception {
        Page<CustomerDTO> page = new PageImpl<>(List.of());

        PageRequest expectedPageRequest =
                PageRequest.of(0, 3, Sort.Direction.ASC, "name");

        Mockito.when(service.findAllPaged(eq(""), eq(expectedPageRequest)))
                .thenReturn(page);

        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements", is(0)));

        Mockito.verify(service).findAllPaged(eq(""), eq(expectedPageRequest));
    }

    @Test
    public void findAllPagedShouldTrimName() throws Exception {
        Page<CustomerDTO> page = new PageImpl<>(List.of());

        PageRequest expectedPageRequest =
                PageRequest.of(0, 3, Sort.Direction.ASC, "name");

        Mockito.when(service.findAllPaged(eq("João"), eq(expectedPageRequest)))
                .thenReturn(page);

        mockMvc.perform(get("/customers")
                        .param("name", "   João   "))
                .andExpect(status().isOk());

        Mockito.verify(service).findAllPaged(eq("João"), eq(expectedPageRequest));
    }

    @Test
    public void findByIdShouldReturnCustomerDetailsDTOWhenIdExists() throws Exception {
        CustomerDetailsDTO dto = CustomerFactory.createCustomerDetailsDTO();

        Mockito.when(service.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/customers/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Maria Silva")))
                .andExpect(jsonPath("$.cpf", is("12345678900")))
                .andExpect(jsonPath("$.email", is("maria@email.com")));

        Mockito.verify(service).findById(1L);
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        Mockito.when(service.findById(999L))
                .thenThrow(new ResourceNotFoundException("Id not found 999"));

        mockMvc.perform(get("/customers/{id}", 999L))
                .andExpect(status().isNotFound());

        Mockito.verify(service).findById(999L);
    }

    @Test
    public void findByIdShouldCallServiceWithCorrectId() throws Exception {
        CustomerDetailsDTO dto = CustomerFactory.createCustomerDetailsDTO();

        Mockito.when(service.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/customers/{id}", 1L))
                .andExpect(status().isOk());

        Mockito.verify(service, Mockito.times(1)).findById(1L);
    }

    @Test
    @WithMockUser
    public void insertShouldReturnCreatedAndCustomerDTO() throws Exception {
        CustomerInsertDTO insertDTO = CustomerFactory.createCustomerInsertDTO();
        CustomerDTO responseDTO = CustomerFactory.createCustomerDTO();

        Mockito.when(repository.existsByCpf(insertDTO.getCpf())).thenReturn(false);
        Mockito.when(repository.existsByEmail(insertDTO.getEmail())).thenReturn(false);

        Mockito.when(service.insert(ArgumentMatchers.any(CustomerInsertDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/customers")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(insertDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Maria Silva")))
                .andExpect(jsonPath("$.cpf", is("12345678900")))
                .andExpect(jsonPath("$.email", is("maria@email.com")));

        Mockito.verify(service).insert(ArgumentMatchers.any(CustomerInsertDTO.class));
    }

    @Test
    @WithMockUser
    public void insertShouldReturnLocationHeader() throws Exception {
        CustomerInsertDTO insertDTO = CustomerFactory.createCustomerInsertDTO();
        CustomerDTO responseDTO = CustomerFactory.createCustomerDTO();

        Mockito.when(repository.existsByCpf(insertDTO.getCpf())).thenReturn(false);
        Mockito.when(repository.existsByEmail(insertDTO.getEmail())).thenReturn(false);

        Mockito.when(service.insert(ArgumentMatchers.any(CustomerInsertDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/customers")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(insertDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/customers/1"));
    }

    @Test
    @WithMockUser
    public void insertShouldReturnUnprocessableEntityWhenInvalidData() throws Exception {
        CustomerInsertDTO insertDTO = new CustomerInsertDTO();

        mockMvc.perform(post("/customers")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(insertDTO)))
                .andExpect(status().isUnprocessableEntity());

        Mockito.verify(service, Mockito.never()).insert(ArgumentMatchers.any());
    }

    @Test
    @WithMockUser
    public void updateShouldReturnOkAndCustomerDTO() throws Exception {
        Long existingId = 1L;

        CustomerUpdateDTO updateDTO = CustomerFactory.createCustomerUpdateDTO();
        CustomerDTO responseDTO = CustomerFactory.createCustomerDTO();
        responseDTO.setName(updateDTO.getName());
        responseDTO.setEmail(updateDTO.getEmail());

        Mockito.when(repository.existsByCpf(updateDTO.getCpf())).thenReturn(false);
        Mockito.when(repository.existsByEmail(updateDTO.getEmail())).thenReturn(false);

        Mockito.when(service.update(eq(existingId), ArgumentMatchers.any(CustomerUpdateDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(put("/customers/{id}", existingId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(existingId.intValue())))
                .andExpect(jsonPath("$.name", is("Maria Atualizada")))
                .andExpect(jsonPath("$.email", is("maria.atualizada@email.com")));

        Mockito.verify(service).update(eq(existingId), ArgumentMatchers.any(CustomerUpdateDTO.class));
    }

    @Test
    @WithMockUser
    public void updateShouldReturnUnprocessableEntityWhenInvalidData() throws Exception {
        Long existingId = 1L;

        CustomerUpdateDTO updateDTO = new CustomerUpdateDTO();

        mockMvc.perform(put("/customers/{id}", existingId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isUnprocessableEntity());

        Mockito.verify(service, Mockito.never()).update(eq(existingId), ArgumentMatchers.any());
    }

    @Test
    @WithMockUser
    public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        Long nonExistingId = 999L;

        CustomerUpdateDTO updateDTO = CustomerFactory.createCustomerUpdateDTO();

        Mockito.when(repository.existsByCpf(updateDTO.getCpf())).thenReturn(false);
        Mockito.when(repository.existsByEmail(updateDTO.getEmail())).thenReturn(false);

        Mockito.when(service.update(eq(nonExistingId), ArgumentMatchers.any(CustomerUpdateDTO.class)))
                .thenThrow(new ResourceNotFoundException("Id not found " + nonExistingId));

        mockMvc.perform(put("/customers/{id}", nonExistingId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());

        Mockito.verify(service).update(eq(nonExistingId), ArgumentMatchers.any(CustomerUpdateDTO.class));
    }

    @Test
    @WithMockUser
    public void updatePhotoShouldReturnNoContentWhenFileExists() throws Exception {
        Long existingId = 1L;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.jpg",
                "image/jpeg",
                "fake-image-content".getBytes()
        );

        Mockito.doNothing().when(service).updatePhoto(eq(existingId), ArgumentMatchers.any());

        mockMvc.perform(multipart("/customers/{id}/photo", existingId)
                        .file(file)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .with(csrf()))
                .andExpect(status().isNoContent());

        Mockito.verify(service).updatePhoto(eq(existingId), ArgumentMatchers.any());
    }

    @Test
    @WithMockUser
    public void updatePhotoShouldReturnNotFoundWhenCustomerDoesNotExist() throws Exception {
        Long nonExistingId = 999L;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.jpg",
                "image/jpeg",
                "fake-image-content".getBytes()
        );

        Mockito.doThrow(new ResourceNotFoundException("Id not found " + nonExistingId))
                .when(service).updatePhoto(eq(nonExistingId), ArgumentMatchers.any());

        mockMvc.perform(multipart("/customers/{id}/photo", nonExistingId)
                        .file(file)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .with(csrf()))
                .andExpect(status().isNotFound());

        Mockito.verify(service).updatePhoto(eq(nonExistingId), ArgumentMatchers.any());
    }

    @Test
    @WithMockUser
    public void updatePhotoShouldReturnBadRequestWhenFileParamIsMissing() throws Exception {
        Long existingId = 1L;

        mockMvc.perform(multipart("/customers/{id}/photo", existingId)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        Mockito.verify(service, Mockito.never()).updatePhoto(eq(existingId), ArgumentMatchers.any());
    }

    @Test
    public void getPhotoShouldReturnPhotoWhenExists() throws Exception {
        Long existingId = 1L;

        Customer customer = CustomerFactory.createCustomer(existingId);
        customer.setPhoto("fake-image-content".getBytes());
        customer.setPhotoContentType("image/jpeg");

        Mockito.when(service.findEntityById(existingId)).thenReturn(customer);

        mockMvc.perform(get("/customers/{id}/photo", existingId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/jpeg"))
                .andExpect(content().bytes("fake-image-content".getBytes()));

        Mockito.verify(service).findEntityById(existingId);
    }

    @Test
    public void getPhotoShouldReturnNoContentWhenPhotoIsNull() throws Exception {
        Long existingId = 1L;

        Customer customer = CustomerFactory.createCustomer(existingId);
        customer.setPhoto(null);
        customer.setPhotoContentType("image/jpeg");

        Mockito.when(service.findEntityById(existingId)).thenReturn(customer);

        mockMvc.perform(get("/customers/{id}/photo", existingId))
                .andExpect(status().isNoContent());

        Mockito.verify(service).findEntityById(existingId);
    }

    @Test
    public void getPhotoShouldReturnNoContentWhenPhotoIsEmpty() throws Exception {
        Long existingId = 1L;

        Customer customer = CustomerFactory.createCustomer(existingId);
        customer.setPhoto(new byte[0]);
        customer.setPhotoContentType("image/jpeg");

        Mockito.when(service.findEntityById(existingId)).thenReturn(customer);

        mockMvc.perform(get("/customers/{id}/photo", existingId))
                .andExpect(status().isNoContent());

        Mockito.verify(service).findEntityById(existingId);
    }

    @Test
    @WithMockUser
    public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
        Long existingId = 1L;

        Mockito.doNothing().when(service).delete(existingId);

        mockMvc.perform(delete("/customers/{id}", existingId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        Mockito.verify(service).delete(existingId);
    }

    @Test
    @WithMockUser
    public void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        Long nonExistingId = 999L;

        Mockito.doThrow(new ResourceNotFoundException("Id not found " + nonExistingId))
                .when(service).delete(nonExistingId);

        mockMvc.perform(delete("/customers/{id}", nonExistingId)
                        .with(csrf()))
                .andExpect(status().isNotFound());

        Mockito.verify(service).delete(nonExistingId);
    }


    @Test
    @WithMockUser
    public void deleteAllShouldReturnNoContentWhenIdsExist() throws Exception {
        List<Long> ids = List.of(1L, 2L);

        Mockito.doNothing().when(service).deleteAll(ids);

        mockMvc.perform(delete("/customers/all")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isNoContent());

        Mockito.verify(service).deleteAll(ids);
    }

    @Test
    @WithMockUser
    public void deleteAllShouldReturnBadRequestWhenListIsEmpty() throws Exception {
        List<Long> ids = List.of();

        Mockito.doThrow(new IllegalArgumentException("Lista de ids vazia"))
                .when(service).deleteAll(ids);

        mockMvc.perform(delete("/customers/all")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isBadRequest());

        Mockito.verify(service).deleteAll(ids);
    }

    @Test
    @WithMockUser
    public void deleteAllShouldReturnNotFoundWhenIdsDoNotExist() throws Exception {
        List<Long> ids = List.of(1L, 2L);

        Mockito.doThrow(new ResourceNotFoundException("Um ou mais IDs não existem"))
                .when(service).deleteAll(ids);

        mockMvc.perform(delete("/customers/all")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isNotFound());

        Mockito.verify(service).deleteAll(ids);
    }

    @Test
    @WithMockUser
    public void changeActiveShouldReturnNoContentWhenIdExists() throws Exception {
        Long existingId = 1L;
        boolean active = true;

        Mockito.doNothing().when(service).changeActiveStatus(existingId, active);

        mockMvc.perform(patch("/customers/{id}/active", existingId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(active)))
                .andExpect(status().isNoContent());

        Mockito.verify(service).changeActiveStatus(existingId, active);
    }

    @Test
    @WithMockUser
    public void changeActiveShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        Long nonExistingId = 999L;
        boolean active = true;

        Mockito.doThrow(new ResourceNotFoundException("Id not found " + nonExistingId))
                .when(service).changeActiveStatus(nonExistingId, active);

        mockMvc.perform(patch("/customers/{id}/active", nonExistingId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(active)))
                .andExpect(status().isNotFound());

        Mockito.verify(service).changeActiveStatus(nonExistingId, active);
    }

    @Test
    @WithMockUser
    public void changeActiveShouldReturnInternalServerErrorWhenDatabaseErrorOccurs() throws Exception {
        Long existingId = 1L;
        boolean active = true;

        Mockito.doThrow(new RuntimeException("Error changing customer status."))
                .when(service).changeActiveStatus(existingId, active);

        mockMvc.perform(patch("/customers/{id}/active", existingId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(active)))
                .andExpect(status().isInternalServerError());

        Mockito.verify(service).changeActiveStatus(existingId, active);
    }
}