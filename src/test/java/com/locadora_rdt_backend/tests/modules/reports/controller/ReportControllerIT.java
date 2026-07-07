package com.locadora_rdt_backend.tests.modules.reports.controller;

import com.locadora_rdt_backend.modules.reports.dto.ReportComparisonDTO;
import com.locadora_rdt_backend.modules.reports.controller.ReportController;
import com.locadora_rdt_backend.modules.reports.dto.ReportFileDTO;
import com.locadora_rdt_backend.modules.reports.dto.ReportFilterDTO;
import com.locadora_rdt_backend.modules.reports.service.ReportService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReportControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService service;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void generateShouldReturnReportFile() throws Exception {
        Mockito.when(service.generate(eq("receivables"), eq("pdf"), any(ReportFilterDTO.class)))
                .thenReturn(new ReportFileDTO("receivables.pdf", "application/pdf", new byte[]{1, 2, 3}));

        mockMvc.perform(get("/reports/receivables/pdf")
                        .param("startDate", "2026-07-01")
                        .param("endDate", "2026-07-31")
                        .param("status", "PAID")
                        .param("customerId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=receivables.pdf"))
                .andExpect(content().bytes(new byte[]{1, 2, 3}));

        ArgumentCaptor<ReportFilterDTO> captor = ArgumentCaptor.forClass(ReportFilterDTO.class);
        Mockito.verify(service).generate(eq("receivables"), eq("pdf"), captor.capture());
        assertEquals(LocalDate.of(2026, 7, 1), captor.getValue().getStartDate());
        assertEquals(LocalDate.of(2026, 7, 31), captor.getValue().getEndDate());
        assertEquals("PAID", captor.getValue().getStatus());
        assertEquals(1L, captor.getValue().getCustomerId());
    }

    @Test
    void comparisonShouldReturnTotals() throws Exception {
        Mockito.when(service.comparison(any(ReportFilterDTO.class)))
                .thenReturn(new ReportComparisonDTO(
                        new BigDecimal("100.00"),
                        new BigDecimal("40.00"),
                        new BigDecimal("60.00"),
                        2,
                        1,
                        2026,
                        Collections.emptyList()
                ));

        mockMvc.perform(get("/reports/comparison")
                        .param("status", "PAID"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"receivableTotal\":100.00,\"payableTotal\":40.00,\"balance\":60.00,\"receivableCount\":2,\"payableCount\":1}"));
    }
}
