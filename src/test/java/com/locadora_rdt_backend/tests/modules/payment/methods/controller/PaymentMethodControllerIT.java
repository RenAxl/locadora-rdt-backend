package com.locadora_rdt_backend.tests.modules.payment.methods.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locadora_rdt_backend.modules.financial.payment.methods.controller.PaymentMethodController;
import com.locadora_rdt_backend.modules.financial.payment.methods.dto.PaymentMethodDTO;
import com.locadora_rdt_backend.modules.financial.payment.methods.dto.PaymentMethodDetailsDTO;
import com.locadora_rdt_backend.modules.financial.payment.methods.dto.PaymentMethodInsertDTO;
import com.locadora_rdt_backend.modules.financial.payment.methods.dto.PaymentMethodUpdateDTO;
import com.locadora_rdt_backend.modules.financial.payment.methods.service.PaymentMethodService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentMethodController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentMethodControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentMethodService service;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private UserDetailsService userDetailsService;

    private final Long existingId = 1L;
    private final Long nonExistingId = 999L;

    @Test
    void findAllPagedShouldReturnPage() throws Exception {
        Mockito.when(service.findAllPaged(anyString(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(createDTO())));

        mockMvc.perform(get("/payment-methods"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(existingId))
                .andExpect(jsonPath("$.content[0].name").value("Pix"));
    }

    @Test
    void findAllPagedShouldCallServiceWithParams() throws Exception {
        Mockito.when(service.findAllPaged(anyString(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/payment-methods")
                        .param("name", " Pix ")
                        .param("page", "1")
                        .param("linesPerPage", "5")
                        .param("direction", "DESC")
                        .param("orderBy", "id"))
                .andExpect(status().isOk());

        Mockito.verify(service).findAllPaged(eq("Pix"), any(PageRequest.class));
    }

    @Test
    void findByIdShouldReturnDetailsDTOWhenIdExists() throws Exception {
        Mockito.when(service.findById(existingId)).thenReturn(createDetailsDTO());

        mockMvc.perform(get("/payment-methods/{id}", existingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingId))
                .andExpect(jsonPath("$.createdBy").value("SYSTEM"));
    }

    @Test
    void findByIdShouldReturnErrorWhenServiceThrowsException() throws Exception {
        Mockito.when(service.findById(nonExistingId)).thenThrow(new RuntimeException());

        mockMvc.perform(get("/payment-methods/{id}", nonExistingId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void insertShouldReturnCreatedAndLocation() throws Exception {
        Mockito.when(service.insert(any())).thenReturn(createDTO());

        mockMvc.perform(post("/payment-methods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createInsertDTO())))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(existingId))
                .andExpect(jsonPath("$.name").value("Pix"));
    }

    @Test
    void insertShouldCallService() throws Exception {
        Mockito.when(service.insert(any())).thenReturn(createDTO());

        mockMvc.perform(post("/payment-methods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createInsertDTO())))
                .andExpect(status().isCreated());

        Mockito.verify(service).insert(any());
    }

    @Test
    void updateShouldReturnUpdatedDTO() throws Exception {
        PaymentMethodDTO updatedDTO = new PaymentMethodDTO(existingId, "Cartao de credito", new BigDecimal("2.50"));

        Mockito.when(service.update(eq(existingId), any())).thenReturn(updatedDTO);

        mockMvc.perform(put("/payment-methods/{id}", existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUpdateDTO())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cartao de credito"))
                .andExpect(jsonPath("$.fee").value(2.50));
    }

    @Test
    void updateShouldCallService() throws Exception {
        Mockito.when(service.update(eq(existingId), any())).thenReturn(createDTO());

        mockMvc.perform(put("/payment-methods/{id}", existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUpdateDTO())))
                .andExpect(status().isOk());

        Mockito.verify(service).update(eq(existingId), any());
    }

    @Test
    void deleteShouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(service).delete(existingId);

        mockMvc.perform(delete("/payment-methods/{id}", existingId))
                .andExpect(status().isNoContent());

        Mockito.verify(service).delete(existingId);
    }

    @Test
    void deleteShouldReturnErrorWhenServiceThrowsException() throws Exception {
        Mockito.doThrow(new RuntimeException()).when(service).delete(nonExistingId);

        mockMvc.perform(delete("/payment-methods/{id}", nonExistingId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void deleteAllShouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(service).deleteAll(anyList());

        mockMvc.perform(delete("/payment-methods/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1,2]"))
                .andExpect(status().isNoContent());

        Mockito.verify(service).deleteAll(List.of(1L, 2L));
    }

    private PaymentMethodDTO createDTO() {
        return new PaymentMethodDTO(existingId, "Pix", new BigDecimal("0.00"));
    }

    private PaymentMethodDetailsDTO createDetailsDTO() {
        PaymentMethodDetailsDTO dto = new PaymentMethodDetailsDTO();
        dto.setId(existingId);
        dto.setName("Pix");
        dto.setFee(new BigDecimal("0.00"));
        dto.setCreatedAt(Instant.parse("2026-01-01T10:00:00Z"));
        dto.setCreatedBy("SYSTEM");
        return dto;
    }

    private PaymentMethodInsertDTO createInsertDTO() {
        PaymentMethodInsertDTO dto = new PaymentMethodInsertDTO();
        dto.setName("Pix");
        dto.setFee(new BigDecimal("0.00"));
        return dto;
    }

    private PaymentMethodUpdateDTO createUpdateDTO() {
        PaymentMethodUpdateDTO dto = new PaymentMethodUpdateDTO();
        dto.setId(existingId);
        dto.setName("Cartao de credito");
        dto.setFee(new BigDecimal("2.50"));
        return dto;
    }
}
