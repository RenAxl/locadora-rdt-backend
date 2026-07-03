package com.locadora_rdt_backend.tests.modules.receivables.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locadora_rdt_backend.modules.financial.receivables.controller.ReceivableController;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableDetailsDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableFilterDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableInsertDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableInstallmentDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivablePaymentDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableReportDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableUpdateDTO;
import com.locadora_rdt_backend.modules.financial.receivables.service.ReceivableService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    void findAllPagedShouldReturnPage() throws Exception {
        Mockito.when(service.findAllPaged(any(ReceivableFilterDTO.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(createDTO())));

        mockMvc.perform(get("/receivables"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].description").value("Movie rental"))
                .andExpect(jsonPath("$.content[0].amount").value(45.90));
    }

    @Test
    void findAllPagedShouldCallServiceWithParams() throws Exception {
        Mockito.when(service.findAllPaged(any(ReceivableFilterDTO.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/receivables")
                        .param("description", " Movie ")
                        .param("status", "PAID")
                        .param("periodType", "PAYMENT_DATE")
                        .param("page", "1")
                        .param("linesPerPage", "5")
                        .param("direction", "DESC")
                        .param("orderBy", "amount"))
                .andExpect(status().isOk());

        ArgumentCaptor<ReceivableFilterDTO> filtersCaptor = ArgumentCaptor.forClass(ReceivableFilterDTO.class);
        ArgumentCaptor<PageRequest> pageRequestCaptor = ArgumentCaptor.forClass(PageRequest.class);

        Mockito.verify(service).findAllPaged(filtersCaptor.capture(), pageRequestCaptor.capture());

        Assertions.assertEquals("Movie", filtersCaptor.getValue().getSearch());
        Assertions.assertEquals("PAID", filtersCaptor.getValue().getStatus());
        Assertions.assertEquals("PAYMENT_DATE", filtersCaptor.getValue().getPeriodType());
        Assertions.assertEquals("amount", filtersCaptor.getValue().getOrderBy());
        Assertions.assertEquals("DESC", filtersCaptor.getValue().getDirection());
        Assertions.assertEquals(1, pageRequestCaptor.getValue().getPageNumber());
        Assertions.assertEquals(5, pageRequestCaptor.getValue().getPageSize());
    }

    @Test
    void findByIdShouldReturnDetails() throws Exception {
        ReceivableDetailsDTO dto = new ReceivableDetailsDTO();
        dto.setId(1L);
        dto.setDescription("Movie rental");
        Mockito.when(service.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/receivables/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Movie rental"));
    }

    @Test
    void insertShouldReturnCreated() throws Exception {
        Mockito.when(service.insert(any())).thenReturn(createDTO());

        mockMvc.perform(post("/receivables")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createInsertDTO())))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void updateShouldReturnOk() throws Exception {
        Mockito.when(service.update(eq(1L), any())).thenReturn(createDTO());

        mockMvc.perform(put("/receivables/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUpdateDTO())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void deleteShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/receivables/{id}", 1L))
                .andExpect(status().isNoContent());

        Mockito.verify(service).delete(1L);
    }

    @Test
    void payShouldReturnOk() throws Exception {
        Mockito.when(service.pay(eq(1L), any())).thenReturn(createDTO());

        mockMvc.perform(post("/receivables/{id}/payments", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPaymentDTO())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void installmentShouldReturnList() throws Exception {
        ReceivableInstallmentDTO dto = new ReceivableInstallmentDTO();
        dto.setInstallments(2);
        Mockito.when(service.installment(eq(1L), any())).thenReturn(List.of(createDTO()));

        mockMvc.perform(post("/receivables/{id}/installments", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void reportShouldReturnReport() throws Exception {
        Mockito.when(service.report(any(), any(), any(), any(), any()))
                .thenReturn(new ReceivableReportDTO(1L, new BigDecimal("45.90"), BigDecimal.ZERO, new BigDecimal("45.90")));

        mockMvc.perform(get("/receivables/report")
                        .param("description", "Movie")
                        .param("startDate", "2026-07-01")
                        .param("endDate", "2026-07-31")
                        .param("status", "open")
                        .param("dateType", "due"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(1L));
    }

    @Test
    void receiptShouldReturnPdf() throws Exception {
        Mockito.when(service.receipt(1L)).thenReturn(new byte[]{'%', 'P', 'D', 'F'});

        mockMvc.perform(get("/receivables/{id}/receipt", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=recibo-1.pdf"))
                .andExpect(content().bytes(new byte[]{'%', 'P', 'D', 'F'}));
    }

    @Test
    void fiscalCouponShouldReturnPdf() throws Exception {
        Mockito.when(service.fiscalCoupon(1L)).thenReturn(new byte[]{'%', 'P', 'D', 'F'});

        mockMvc.perform(get("/receivables/{id}/fiscal-coupon", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=cupom-fiscal-1.pdf"))
                .andExpect(content().bytes(new byte[]{'%', 'P', 'D', 'F'}));
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

    private ReceivableInsertDTO createInsertDTO() {
        ReceivableInsertDTO dto = new ReceivableInsertDTO();
        dto.setDescription("Movie rental");
        dto.setAmount(new BigDecimal("45.90"));
        dto.setDueDate(LocalDate.of(2026, 7, 1));
        dto.setCustomerId(1L);
        dto.setPaymentFrequencyId(1L);
        return dto;
    }

    private ReceivableUpdateDTO createUpdateDTO() {
        ReceivableUpdateDTO dto = new ReceivableUpdateDTO();
        dto.setId(1L);
        dto.setDescription("Movie rental updated");
        dto.setAmount(new BigDecimal("50.00"));
        dto.setCustomerId(1L);
        dto.setPaymentFrequencyId(1L);
        return dto;
    }

    private ReceivablePaymentDTO createPaymentDTO() {
        ReceivablePaymentDTO dto = new ReceivablePaymentDTO();
        dto.setPaymentAmount(new BigDecimal("45.90"));
        dto.setPaymentDate(LocalDate.of(2026, 7, 1));
        return dto;
    }
}
