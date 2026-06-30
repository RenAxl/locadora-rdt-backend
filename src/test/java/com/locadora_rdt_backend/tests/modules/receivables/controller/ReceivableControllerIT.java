package com.locadora_rdt_backend.tests.modules.receivables.controller;

import com.locadora_rdt_backend.modules.receivables.controller.ReceivableController;
import com.locadora_rdt_backend.modules.receivables.dto.ReceivableDTO;
import com.locadora_rdt_backend.modules.receivables.service.ReceivableService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReceivableController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReceivableControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReceivableService service;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void findAllPagedShouldReturnPage() throws Exception {
        Mockito.when(service.findAllPaged(anyString(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(createDTO())));

        mockMvc.perform(get("/receivables"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].description").value("Movie rental"))
                .andExpect(jsonPath("$.content[0].amount").value(45.90));
    }

    @Test
    void findAllPagedShouldCallServiceWithParams() throws Exception {
        Mockito.when(service.findAllPaged(anyString(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/receivables")
                        .param("description", " Movie ")
                        .param("page", "1")
                        .param("linesPerPage", "5")
                        .param("direction", "DESC")
                        .param("orderBy", "id"))
                .andExpect(status().isOk());

        Mockito.verify(service).findAllPaged(eq("Movie"), any(PageRequest.class));
    }

    private ReceivableDTO createDTO() {
        ReceivableDTO dto = new ReceivableDTO();
        dto.setId(1L);
        dto.setDescription("Movie rental");
        dto.setAmount(new BigDecimal("45.90"));
        dto.setPaid(false);
        dto.setCustomerId(1L);
        dto.setCustomerName("Cliente");
        return dto;
    }
}
