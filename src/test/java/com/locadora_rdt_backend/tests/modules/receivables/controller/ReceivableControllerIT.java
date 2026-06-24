package com.locadora_rdt_backend.tests.modules.receivables.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locadora_rdt_backend.modules.receivables.controller.ReceivableController;
import com.locadora_rdt_backend.modules.receivables.dto.ReceivableDTO;
import com.locadora_rdt_backend.modules.receivables.dto.ReceivableInsertDTO;
import com.locadora_rdt_backend.modules.receivables.model.ReceivableStatus;
import com.locadora_rdt_backend.modules.receivables.service.ReceivableService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReceivableController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReceivableControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReceivableService service;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void insertShouldReturnCreated() throws Exception {
        Mockito.when(service.insert(any())).thenReturn(createReceivableDTO());

        mockMvc.perform(post("/receivables")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createInsertDTO())))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void insertShouldReturnCreatedOnPortugueseAlias() throws Exception {
        Mockito.when(service.insert(any())).thenReturn(createReceivableDTO());

        mockMvc.perform(post("/receber")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createInsertDTO())))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    private ReceivableInsertDTO createInsertDTO() {
        ReceivableInsertDTO dto = new ReceivableInsertDTO();
        dto.setCustomerId(1L);
        dto.setAmount(new BigDecimal("150.00"));
        dto.setDueDate(LocalDate.of(2026, 7, 10));
        return dto;
    }

    private ReceivableDTO createReceivableDTO() {
        ReceivableDTO dto = new ReceivableDTO();
        dto.setId(1L);
        dto.setCustomerId(1L);
        dto.setAmount(new BigDecimal("150.00"));
        dto.setDueDate(LocalDate.of(2026, 7, 10));
        dto.setStatus(ReceivableStatus.PENDING);
        return dto;
    }
}
