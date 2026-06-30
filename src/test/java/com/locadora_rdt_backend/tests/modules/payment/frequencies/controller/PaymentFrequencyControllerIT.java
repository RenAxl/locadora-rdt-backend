package com.locadora_rdt_backend.tests.modules.payment.frequencies.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locadora_rdt_backend.modules.payment.frequencies.controller.PaymentFrequencyController;
import com.locadora_rdt_backend.modules.payment.frequencies.dto.PaymentFrequencyDTO;
import com.locadora_rdt_backend.modules.payment.frequencies.dto.PaymentFrequencyDetailsDTO;
import com.locadora_rdt_backend.modules.payment.frequencies.dto.PaymentFrequencyInsertDTO;
import com.locadora_rdt_backend.modules.payment.frequencies.dto.PaymentFrequencyUpdateDTO;
import com.locadora_rdt_backend.modules.payment.frequencies.service.PaymentFrequencyService;
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

@WebMvcTest(PaymentFrequencyController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentFrequencyControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentFrequencyService service;

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

        mockMvc.perform(get("/payment-frequencies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(existingId))
                .andExpect(jsonPath("$.content[0].frequency").value("Mensal"));
    }

    @Test
    void findAllPagedShouldCallServiceWithParams() throws Exception {
        Mockito.when(service.findAllPaged(anyString(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/payment-frequencies")
                        .param("frequency", " Mensal ")
                        .param("page", "1")
                        .param("linesPerPage", "5")
                        .param("direction", "DESC")
                        .param("orderBy", "id"))
                .andExpect(status().isOk());

        Mockito.verify(service).findAllPaged(eq("Mensal"), any(PageRequest.class));
    }

    @Test
    void findByIdShouldReturnDetailsDTOWhenIdExists() throws Exception {
        Mockito.when(service.findById(existingId)).thenReturn(createDetailsDTO());

        mockMvc.perform(get("/payment-frequencies/{id}", existingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingId))
                .andExpect(jsonPath("$.createdBy").value("SYSTEM"));
    }

    @Test
    void findByIdShouldReturnErrorWhenServiceThrowsException() throws Exception {
        Mockito.when(service.findById(nonExistingId)).thenThrow(new RuntimeException());

        mockMvc.perform(get("/payment-frequencies/{id}", nonExistingId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void insertShouldReturnCreatedAndLocation() throws Exception {
        Mockito.when(service.insert(any())).thenReturn(createDTO());

        mockMvc.perform(post("/payment-frequencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createInsertDTO())))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(existingId))
                .andExpect(jsonPath("$.frequency").value("Mensal"));
    }

    @Test
    void insertShouldReturnBadRequestWhenInvalid() throws Exception {
        PaymentFrequencyInsertDTO invalidDTO = createInsertDTO();
        invalidDTO.setFrequency("");
        invalidDTO.setDays(-1);

        mockMvc.perform(post("/payment-frequencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isUnprocessableEntity());

        Mockito.verify(service, Mockito.never()).insert(any());
    }

    @Test
    void updateShouldReturnUpdatedDTO() throws Exception {
        PaymentFrequencyDTO updatedDTO = new PaymentFrequencyDTO(existingId, "Quinzenal", 15);

        Mockito.when(service.update(eq(existingId), any())).thenReturn(updatedDTO);

        mockMvc.perform(put("/payment-frequencies/{id}", existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUpdateDTO())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.frequency").value("Quinzenal"))
                .andExpect(jsonPath("$.days").value(15));
    }

    @Test
    void updateShouldReturnBadRequestWhenInvalid() throws Exception {
        PaymentFrequencyUpdateDTO invalidDTO = createUpdateDTO();
        invalidDTO.setFrequency("AB");
        invalidDTO.setDays(null);

        mockMvc.perform(put("/payment-frequencies/{id}", existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isUnprocessableEntity());

        Mockito.verify(service, Mockito.never()).update(eq(existingId), any());
    }

    @Test
    void deleteShouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(service).delete(existingId);

        mockMvc.perform(delete("/payment-frequencies/{id}", existingId))
                .andExpect(status().isNoContent());

        Mockito.verify(service).delete(existingId);
    }

    @Test
    void deleteShouldReturnErrorWhenServiceThrowsException() throws Exception {
        Mockito.doThrow(new RuntimeException()).when(service).delete(nonExistingId);

        mockMvc.perform(delete("/payment-frequencies/{id}", nonExistingId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void deleteAllShouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(service).deleteAll(anyList());

        mockMvc.perform(delete("/payment-frequencies/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1,2]"))
                .andExpect(status().isNoContent());

        Mockito.verify(service).deleteAll(List.of(1L, 2L));
    }

    private PaymentFrequencyDTO createDTO() {
        return new PaymentFrequencyDTO(existingId, "Mensal", 30);
    }

    private PaymentFrequencyDetailsDTO createDetailsDTO() {
        PaymentFrequencyDetailsDTO dto = new PaymentFrequencyDetailsDTO();
        dto.setId(existingId);
        dto.setFrequency("Mensal");
        dto.setDays(30);
        dto.setCreatedAt(Instant.parse("2026-01-01T10:00:00Z"));
        dto.setCreatedBy("SYSTEM");
        return dto;
    }

    private PaymentFrequencyInsertDTO createInsertDTO() {
        PaymentFrequencyInsertDTO dto = new PaymentFrequencyInsertDTO();
        dto.setFrequency("Mensal");
        dto.setDays(30);
        return dto;
    }

    private PaymentFrequencyUpdateDTO createUpdateDTO() {
        PaymentFrequencyUpdateDTO dto = new PaymentFrequencyUpdateDTO();
        dto.setId(existingId);
        dto.setFrequency("Quinzenal");
        dto.setDays(15);
        return dto;
    }
}
