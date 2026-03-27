package com.locadora_rdt_backend.tests.modules.customers.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.customers.controller.CustomerController;
import com.locadora_rdt_backend.modules.customers.dto.CustomerDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerInsertDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerUpdateDTO;
import com.locadora_rdt_backend.modules.customers.model.Customer;
import com.locadora_rdt_backend.modules.customers.repository.CustomerRepository;
import com.locadora_rdt_backend.modules.customers.service.CustomerService;
import com.locadora_rdt_backend.tests.factories.CustomerFactory;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

        Mockito.verify(service, Mockito.times(1))
                .findAllPaged(eq("Maria"), eq(expectedPageRequest));
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

        Mockito.verify(service, Mockito.times(1))
                .findAllPaged(eq(""), eq(expectedPageRequest));
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

        Mockito.verify(service, Mockito.times(1))
                .findAllPaged(eq("João"), eq(expectedPageRequest));
    }

    @Test
    @WithMockUser
    public void insertShouldReturnCreatedAndCustomerDTO() throws Exception {
        CustomerInsertDTO insertDTO = new CustomerInsertDTO();
        insertDTO.setName("Maria Silva");
        insertDTO.setCpf("52998224725");
        insertDTO.setEmail("maria@email.com");
        insertDTO.setPhone("31999999999");
        insertDTO.setAddress("Rua A, 100");

        CustomerDTO responseDTO = new CustomerDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Maria Silva");
        responseDTO.setCpf("52998224725");
        responseDTO.setEmail("maria@email.com");
        responseDTO.setPhone("31999999999");
        responseDTO.setAddress("Rua A, 100");

        Mockito.when(service.insert(ArgumentMatchers.any(CustomerInsertDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/customers")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(insertDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Maria Silva")))
                .andExpect(jsonPath("$.cpf", is("52998224725")))
                .andExpect(jsonPath("$.email", is("maria@email.com")));

        Mockito.verify(service, Mockito.times(1))
                .insert(ArgumentMatchers.any(CustomerInsertDTO.class));
    }

    @Test
    @WithMockUser
    public void insertShouldReturnLocationHeader() throws Exception {
        CustomerInsertDTO insertDTO = new CustomerInsertDTO();
        insertDTO.setName("Maria Silva");
        insertDTO.setCpf("52998224725");
        insertDTO.setEmail("maria@email.com");
        insertDTO.setPhone("31999999999");
        insertDTO.setAddress("Rua A, 100");

        CustomerDTO responseDTO = new CustomerDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Maria Silva");
        responseDTO.setCpf("52998224725");
        responseDTO.setEmail("maria@email.com");
        responseDTO.setPhone("31999999999");
        responseDTO.setAddress("Rua A, 100");

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
    public void updatePhotoShouldReturnNoContent() throws Exception {
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

        Mockito.verify(service, Mockito.times(1))
                .updatePhoto(eq(existingId), ArgumentMatchers.any());
    }

    @Test
    public void getPhotoShouldReturnPhotoWhenExists() throws Exception {
        Long existingId = 1L;

        Customer customer = new Customer();
        customer.setId(existingId);
        customer.setPhoto("fake-image-content".getBytes());
        customer.setPhotoContentType("image/jpeg");

        Mockito.when(service.findEntityById(existingId))
                .thenReturn(customer);

        mockMvc.perform(get("/customers/{id}/photo", existingId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/jpeg"))
                .andExpect(content().bytes("fake-image-content".getBytes()));

        Mockito.verify(service, Mockito.times(1)).findEntityById(existingId);
    }

    @Test
    public void getPhotoShouldReturnNoContentWhenPhotoIsNull() throws Exception {
        Long existingId = 1L;

        Customer customer = new Customer();
        customer.setId(existingId);
        customer.setPhoto(null);
        customer.setPhotoContentType("image/jpeg");

        Mockito.when(service.findEntityById(existingId))
                .thenReturn(customer);

        mockMvc.perform(get("/customers/{id}/photo", existingId))
                .andExpect(status().isNoContent());

        Mockito.verify(service, Mockito.times(1)).findEntityById(existingId);
    }

    @Test
    @WithMockUser
    public void updateShouldReturnOkAndCustomerDTO() throws Exception {
        Long existingId = 1L;

        CustomerUpdateDTO updateDTO = new CustomerUpdateDTO();
        updateDTO.setName("Maria Atualizada");
        updateDTO.setCpf("52998224725");
        updateDTO.setEmail("nova@email.com");
        updateDTO.setPhone("31888888888");
        updateDTO.setAddress("Rua B, 200");

        CustomerDTO responseDTO = new CustomerDTO();
        responseDTO.setId(existingId);
        responseDTO.setName("Maria Atualizada");
        responseDTO.setCpf("52998224725");
        responseDTO.setEmail("nova@email.com");
        responseDTO.setPhone("31888888888");
        responseDTO.setAddress("Rua B, 200");

        Mockito.when(service.update(eq(existingId), ArgumentMatchers.any(CustomerUpdateDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/customers/{id}", existingId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(existingId.intValue())))
                .andExpect(jsonPath("$.name", is("Maria Atualizada")))
                .andExpect(jsonPath("$.email", is("nova@email.com")));

        Mockito.verify(service, Mockito.times(1))
                .update(eq(existingId), ArgumentMatchers.any(CustomerUpdateDTO.class));
    }

    @Test
    @WithMockUser
    public void updateShouldReturnUnprocessableEntityWhenInvalidData() throws Exception {
        Long existingId = 1L;

        CustomerUpdateDTO updateDTO = new CustomerUpdateDTO();
        updateDTO.setName(""); // inválido (@NotBlank)

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/customers/{id}", existingId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isUnprocessableEntity());

        Mockito.verify(service, Mockito.never())
                .update(eq(existingId), ArgumentMatchers.any());
    }

    @Test
    @WithMockUser
    public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        Long nonExistingId = 999L;

        CustomerUpdateDTO updateDTO = new CustomerUpdateDTO();
        updateDTO.setName("Maria");
        updateDTO.setCpf("52998224725"); // ✅ obrigatório
        updateDTO.setEmail("maria@email.com");

        Mockito.when(service.update(eq(nonExistingId), ArgumentMatchers.any(CustomerUpdateDTO.class)))
                .thenThrow(new ResourceNotFoundException("Id not found " + nonExistingId));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/customers/{id}", nonExistingId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());

        Mockito.verify(service, Mockito.times(1))
                .update(eq(nonExistingId), ArgumentMatchers.any(CustomerUpdateDTO.class));
    }

    @Test
    @WithMockUser
    public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
        Long existingId = 1L;

        Mockito.doNothing().when(service).delete(existingId);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .delete("/customers/{id}", existingId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        Mockito.verify(service, Mockito.times(1)).delete(existingId);
    }

    @Test
    @WithMockUser
    public void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        Long nonExistingId = 999L;

        Mockito.doThrow(new ResourceNotFoundException("Id not found " + nonExistingId))
                .when(service).delete(nonExistingId);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .delete("/customers/{id}", nonExistingId)
                        .with(csrf()))
                .andExpect(status().isNotFound());

        Mockito.verify(service, Mockito.times(1)).delete(nonExistingId);
    }

    @Test
    @WithMockUser
    public void deleteShouldCallServiceDelete() throws Exception {
        Long existingId = 1L;

        Mockito.doNothing().when(service).delete(existingId);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .delete("/customers/{id}", existingId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        Mockito.verify(service, Mockito.times(1)).delete(existingId);
    }

}