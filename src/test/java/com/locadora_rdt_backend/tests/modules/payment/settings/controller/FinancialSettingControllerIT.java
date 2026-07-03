package com.locadora_rdt_backend.tests.modules.payment.settings.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locadora_rdt_backend.modules.financial.payment.settings.controller.FinancialSettingController;
import com.locadora_rdt_backend.modules.financial.payment.settings.dto.FinancialSettingDTO;
import com.locadora_rdt_backend.modules.financial.payment.settings.dto.FinancialSettingUpdateDTO;
import com.locadora_rdt_backend.modules.financial.payment.settings.service.FinancialSettingService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FinancialSettingController.class)
@AutoConfigureMockMvc(addFilters = false)
class FinancialSettingControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FinancialSettingService service;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void findCurrentShouldReturnSettings() throws Exception {
        Mockito.when(service.findCurrent()).thenReturn(createDTO());

        mockMvc.perform(get("/financial-settings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.defaultLateFeePercent").value(2.00))
                .andExpect(jsonPath("$.defaultLateInterestPercent").value(1.50));
    }

    @Test
    void updateShouldReturnUpdatedSettings() throws Exception {
        Mockito.when(service.update(any())).thenReturn(createDTO());

        mockMvc.perform(put("/financial-settings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUpdateDTO())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.defaultLateFeePercent").value(2.00))
                .andExpect(jsonPath("$.defaultLateInterestPercent").value(1.50));

        Mockito.verify(service).update(any());
    }

    @Test
    void updateShouldReturnBadRequestWhenPayloadIsInvalid() throws Exception {
        FinancialSettingUpdateDTO dto = new FinancialSettingUpdateDTO();
        dto.setDefaultLateFeePercent(new BigDecimal("-1.00"));
        dto.setDefaultLateInterestPercent(null);

        mockMvc.perform(put("/financial-settings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity());

        Mockito.verify(service, Mockito.never()).update(any());
    }

    private FinancialSettingDTO createDTO() {
        return new FinancialSettingDTO(1L, new BigDecimal("2.00"), new BigDecimal("1.50"));
    }

    private FinancialSettingUpdateDTO createUpdateDTO() {
        FinancialSettingUpdateDTO dto = new FinancialSettingUpdateDTO();
        dto.setDefaultLateFeePercent(new BigDecimal("2.00"));
        dto.setDefaultLateInterestPercent(new BigDecimal("1.50"));
        return dto;
    }
}
