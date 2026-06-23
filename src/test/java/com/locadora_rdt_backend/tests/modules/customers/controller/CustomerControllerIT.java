package com.locadora_rdt_backend.tests.modules.customers.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locadora_rdt_backend.modules.customers.controller.CustomerController;
import com.locadora_rdt_backend.modules.customers.dto.CustomerDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerDetailsDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerInsertDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerUpdateDTO;
import com.locadora_rdt_backend.modules.customers.model.Customer;
import com.locadora_rdt_backend.modules.customers.repository.CustomerRepository;
import com.locadora_rdt_backend.modules.customers.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@AutoConfigureMockMvc(addFilters = false)
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

    private final Long existingId = 1L;

    @Test
    void findAllPagedShouldReturnPage() throws Exception {
        Mockito.when(service.findAllPaged(anyString(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(createCustomerDTO())));

        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(existingId));
    }

    @Test
    void findByIdShouldReturnDTOWhenIdExists() throws Exception {
        Mockito.when(service.findById(existingId)).thenReturn(createDetailsDTO());

        mockMvc.perform(get("/customers/{id}", existingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cliente"));
    }

    @Test
    void insertShouldReturnCreated() throws Exception {
        Mockito.when(service.insert(any())).thenReturn(createCustomerDTO());

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createInsertDTO())))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void updateShouldReturnOK() throws Exception {
        Mockito.when(service.update(eq(existingId), any())).thenReturn(createCustomerDTO());

        mockMvc.perform(put("/customers/{id}", existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUpdateDTO())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingId));
    }

    @Test
    void updatePhotoShouldReturnNoContent() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "photo.png", "image/png", new byte[]{1});

        mockMvc.perform(multipart("/customers/{id}/photo", existingId)
                        .file(file)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isNoContent());

        Mockito.verify(service).updatePhoto(eq(existingId), any());
    }

    @Test
    void getPhotoShouldReturnNoContentWhenCustomerHasNoPhoto() throws Exception {
        Customer customer = new Customer();
        customer.setId(existingId);

        Mockito.when(service.findEntityById(existingId)).thenReturn(customer);

        mockMvc.perform(get("/customers/{id}/photo", existingId))
                .andExpect(status().isNoContent());
    }

    @Test
    void getPhotoShouldReturnBytesWhenCustomerHasPhoto() throws Exception {
        Customer customer = new Customer();
        customer.setId(existingId);
        customer.setPhoto(new byte[]{1});
        customer.setPhotoContentType("image/png");

        Mockito.when(service.findEntityById(existingId)).thenReturn(customer);

        mockMvc.perform(get("/customers/{id}/photo", existingId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(new byte[]{1}));
    }

    @Test
    void deleteShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/customers/{id}", existingId))
                .andExpect(status().isNoContent());

        Mockito.verify(service).delete(existingId);
    }

    @Test
    void deleteAllShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/customers/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(existingId))))
                .andExpect(status().isNoContent());

        Mockito.verify(service).deleteAll(List.of(existingId));
    }

    @Test
    void changeActiveShouldReturnNoContent() throws Exception {
        mockMvc.perform(patch("/customers/{id}/active", existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("false"))
                .andExpect(status().isNoContent());

        Mockito.verify(service).changeActiveStatus(existingId, false);
    }

    private CustomerDTO createCustomerDTO() {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(existingId);
        dto.setName("Cliente");
        return dto;
    }

    private CustomerDetailsDTO createDetailsDTO() {
        CustomerDetailsDTO dto = new CustomerDetailsDTO();
        dto.setId(existingId);
        dto.setName("Cliente");
        return dto;
    }

    private CustomerInsertDTO createInsertDTO() {
        CustomerInsertDTO dto = new CustomerInsertDTO();
        dto.setName("Cliente");
        dto.setCpf("12345678901");
        dto.setEmail("cliente@email.com");
        dto.setPhone("11999999999");
        dto.setAddress("Rua A");
        return dto;
    }

    private CustomerUpdateDTO createUpdateDTO() {
        CustomerUpdateDTO dto = new CustomerUpdateDTO();
        dto.setName("Cliente Atualizado");
        dto.setCpf("12345678901");
        dto.setEmail("cliente@email.com");
        dto.setPhone("11999999999");
        dto.setAddress("Rua A");
        return dto;
    }
}
