package com.locadora_rdt_backend.tests.modules.reports.controller;

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
    void voucherShouldReturnReportFile() throws Exception {
        Mockito.when(service.voucher("payable", 2L, "xlsx"))
                .thenReturn(new ReportFileDTO(
                        "comprovante-payable-2.xlsx",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                        new byte[]{4, 5}
                ));

        mockMvc.perform(get("/reports/vouchers/payable/2/xlsx"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=comprovante-payable-2.xlsx"))
                .andExpect(content().bytes(new byte[]{4, 5}));
    }
}
