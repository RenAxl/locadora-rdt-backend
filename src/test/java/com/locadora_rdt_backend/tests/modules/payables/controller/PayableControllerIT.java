package com.locadora_rdt_backend.tests.modules.payables.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locadora_rdt_backend.modules.financial.payables.controller.PayableController;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableDetailsDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableFilterDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableInsertDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableInstallmentDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayablePaymentDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableReportDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableUpdateDTO;
import com.locadora_rdt_backend.modules.financial.payables.service.PayableService;
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

@WebMvcTest(PayableController.class)
@AutoConfigureMockMvc(addFilters = false)
class PayableControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PayableService service;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void findAllPagedShouldReturnPage() throws Exception {
        Mockito.when(service.findAllPaged(any(PayableFilterDTO.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(createDTO())));

        mockMvc.perform(get("/payables"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].description").value("Movie rental"))
                .andExpect(jsonPath("$.content[0].amount").value(45.90));
    }

    @Test
    void findAllPagedShouldCallServiceWithParams() throws Exception {
        Mockito.when(service.findAllPaged(any(PayableFilterDTO.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/payables")
                        .param("description", " Movie ")
                        .param("status", "PAID")
                        .param("periodType", "PAYMENT_DATE")
                        .param("page", "1")
                        .param("linesPerPage", "5")
                        .param("direction", "DESC")
                        .param("orderBy", "amount"))
                .andExpect(status().isOk());

        ArgumentCaptor<PayableFilterDTO> filtersCaptor = ArgumentCaptor.forClass(PayableFilterDTO.class);
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
        PayableDetailsDTO dto = new PayableDetailsDTO();
        dto.setId(1L);
        dto.setDescription("Movie rental");
        Mockito.when(service.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/payables/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Movie rental"));
    }

    @Test
    void insertShouldReturnCreated() throws Exception {
        Mockito.when(service.insert(any())).thenReturn(createDTO());

        mockMvc.perform(post("/payables")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createInsertDTO())))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void updateShouldReturnOk() throws Exception {
        Mockito.when(service.update(eq(1L), any())).thenReturn(createDTO());

        mockMvc.perform(put("/payables/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUpdateDTO())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void deleteShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/payables/{id}", 1L))
                .andExpect(status().isNoContent());

        Mockito.verify(service).delete(1L);
    }

    @Test
    void payShouldReturnOk() throws Exception {
        Mockito.when(service.pay(eq(1L), any())).thenReturn(createDTO());

        mockMvc.perform(post("/payables/{id}/payments", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPaymentDTO())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void installmentShouldReturnList() throws Exception {
        PayableInstallmentDTO dto = new PayableInstallmentDTO();
        dto.setInstallments(2);
        Mockito.when(service.installment(eq(1L), any())).thenReturn(List.of(createDTO()));

        mockMvc.perform(post("/payables/{id}/installments", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void reportShouldReturnReport() throws Exception {
        Mockito.when(service.report(any(), any(), any(), any(), any()))
                .thenReturn(new PayableReportDTO(1L, new BigDecimal("45.90"), BigDecimal.ZERO, new BigDecimal("45.90")));

        mockMvc.perform(get("/payables/report")
                        .param("description", "Movie")
                        .param("startDate", "2026-07-01")
                        .param("endDate", "2026-07-31")
                        .param("status", "open")
                        .param("dateType", "due"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(1L));
    }

    private PayableDTO createDTO() {
        PayableDTO dto = new PayableDTO();
        dto.setId(1L);
        dto.setDescription("Movie rental");
        dto.setAmount(new BigDecimal("45.90"));
        dto.setPaid(false);
        dto.setSupplierId(1L);
        dto.setSupplierName("Fornecedor");
        return dto;
    }

    private PayableInsertDTO createInsertDTO() {
        PayableInsertDTO dto = new PayableInsertDTO();
        dto.setDescription("Movie rental");
        dto.setAmount(new BigDecimal("45.90"));
        dto.setDueDate(LocalDate.of(2026, 7, 1));
        dto.setSupplierId(1L);
        dto.setPaymentFrequencyId(1L);
        return dto;
    }

    private PayableUpdateDTO createUpdateDTO() {
        PayableUpdateDTO dto = new PayableUpdateDTO();
        dto.setId(1L);
        dto.setDescription("Movie rental updated");
        dto.setAmount(new BigDecimal("50.00"));
        dto.setDueDate(LocalDate.of(2026, 7, 1));
        dto.setSupplierId(1L);
        dto.setPaymentFrequencyId(1L);
        return dto;
    }

    private PayablePaymentDTO createPaymentDTO() {
        PayablePaymentDTO dto = new PayablePaymentDTO();
        dto.setPaymentAmount(new BigDecimal("45.90"));
        dto.setPaymentDate(LocalDate.of(2026, 7, 1));
        return dto;
    }
}
