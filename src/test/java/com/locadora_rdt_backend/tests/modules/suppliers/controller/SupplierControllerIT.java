package com.locadora_rdt_backend.tests.modules.suppliers.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locadora_rdt_backend.modules.suppliers.controller.SupplierController;
import com.locadora_rdt_backend.modules.suppliers.dto.SupplierDTO;
import com.locadora_rdt_backend.modules.suppliers.dto.SupplierDetailsDTO;
import com.locadora_rdt_backend.modules.suppliers.dto.SupplierInsertDTO;
import com.locadora_rdt_backend.modules.suppliers.dto.SupplierUpdateDTO;
import com.locadora_rdt_backend.modules.suppliers.model.Supplier;
import com.locadora_rdt_backend.modules.suppliers.service.SupplierService;
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

@WebMvcTest(SupplierController.class)
@AutoConfigureMockMvc(addFilters = false)
class SupplierControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SupplierService service;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void findAllPagedShouldReturnPage() throws Exception {
        Mockito.when(service.findAllPaged(anyString(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(createDTO())));

        mockMvc.perform(get("/suppliers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    void findByIdShouldReturnDTO() throws Exception {
        SupplierDetailsDTO dto = new SupplierDetailsDTO();
        dto.setId(1L);
        dto.setName("Fornecedor");
        Mockito.when(service.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/suppliers/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Fornecedor"));
    }

    @Test
    void insertShouldReturnCreated() throws Exception {
        Mockito.when(service.insert(any())).thenReturn(createDTO());

        mockMvc.perform(post("/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createInsertDTO())))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void updateShouldReturnOK() throws Exception {
        Mockito.when(service.update(eq(1L), any())).thenReturn(createDTO());

        mockMvc.perform(put("/suppliers/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUpdateDTO())))
                .andExpect(status().isOk());
    }

    @Test
    void updateImageShouldReturnNoContent() throws Exception {
        mockMvc.perform(multipart("/suppliers/{id}/image", 1L)
                        .file(new MockMultipartFile("file", "image.png", "image/png", new byte[]{1}))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isNoContent());
    }

    @Test
    void getImageShouldReturnBytesWhenImageExists() throws Exception {
        Supplier supplier = new Supplier();
        supplier.setImage(new byte[]{1});
        supplier.setImageContentType("image/png");
        Mockito.when(service.findEntityById(1L)).thenReturn(supplier);

        mockMvc.perform(get("/suppliers/{id}/image", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG));
    }

    @Test
    void getImageShouldReturnNoContentWhenImageDoesNotExist() throws Exception {
        Mockito.when(service.findEntityById(1L)).thenReturn(new Supplier());

        mockMvc.perform(get("/suppliers/{id}/image", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/suppliers/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    private SupplierDTO createDTO() {
        SupplierDTO dto = new SupplierDTO();
        dto.setId(1L);
        dto.setName("Fornecedor");
        return dto;
    }

    private SupplierInsertDTO createInsertDTO() {
        SupplierInsertDTO dto = new SupplierInsertDTO();
        fill(dto);
        return dto;
    }

    private SupplierUpdateDTO createUpdateDTO() {
        SupplierUpdateDTO dto = new SupplierUpdateDTO();
        fill(dto);
        return dto;
    }

    private void fill(SupplierInsertDTO dto) {
        dto.setName("Fornecedor");
        dto.setTradeName("Fantasia");
        dto.setCompanyName("Empresa");
        dto.setCnpj("12345678000199");
        dto.setAddress("Rua A");
        dto.setEmail("fornecedor@email.com");
        dto.setPhoneNumber("11999999999");
    }
}
