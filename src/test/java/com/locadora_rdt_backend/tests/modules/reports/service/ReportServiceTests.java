package com.locadora_rdt_backend.tests.modules.reports.service;

import com.locadora_rdt_backend.modules.financial.payables.model.Payable;
import com.locadora_rdt_backend.modules.financial.receivables.model.Receivable;
import com.locadora_rdt_backend.modules.reports.dto.ReportComparisonDTO;
import com.locadora_rdt_backend.modules.reports.dto.ReportFileDTO;
import com.locadora_rdt_backend.modules.reports.dto.ReportFilterDTO;
import com.locadora_rdt_backend.modules.reports.model.ReportFormat;
import com.locadora_rdt_backend.modules.reports.repository.ReportPayableRepository;
import com.locadora_rdt_backend.modules.reports.repository.ReportReceivableRepository;
import com.locadora_rdt_backend.modules.reports.service.JasperReportGenerator;
import com.locadora_rdt_backend.modules.reports.service.ReportServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

class ReportServiceTests {

    private ReportReceivableRepository receivableRepository;
    private ReportPayableRepository payableRepository;
    private JasperReportGenerator generator;
    private ReportServiceImpl service;

    @BeforeEach
    void setup() {
        receivableRepository = Mockito.mock(ReportReceivableRepository.class);
        payableRepository = Mockito.mock(ReportPayableRepository.class);
        generator = Mockito.mock(JasperReportGenerator.class);
        Clock clock = Clock.fixed(Instant.parse("2026-07-07T00:00:00Z"), ZoneOffset.UTC);
        service = new ReportServiceImpl(receivableRepository, payableRepository, generator, clock);
        Mockito.when(generator.generate(ArgumentMatchers.anyString(), ArgumentMatchers.anyList(),
                        ArgumentMatchers.anyList(), ArgumentMatchers.any(ReportFormat.class)))
                .thenReturn(new byte[]{1, 2, 3});
    }

    @Test
    void generateReceivablesShouldReturnPdfFile() {
        Mockito.when(receivableRepository.findForReports(
                        ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(),
                        ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(),
                        ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(List.of(receivable(1L, true)));

        ReportFilterDTO filters = new ReportFilterDTO();
        filters.setStatus("paid");
        ReportFileDTO file = service.generate("receivables", "pdf", filters);

        Assertions.assertEquals("receivables.pdf", file.getFileName());
        Assertions.assertEquals("application/pdf", file.getContentType());
        Assertions.assertArrayEquals(new byte[]{1, 2, 3}, file.getContent());
        Mockito.verify(generator).generate(ArgumentMatchers.eq("Relatório de Contas a Receber"),
                ArgumentMatchers.anyList(), ArgumentMatchers.anyList(), ArgumentMatchers.eq(ReportFormat.PDF));
    }

    @Test
    void generateFinancialShouldReturnXlsxFile() {
        Mockito.when(receivableRepository.findForReports(
                        ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(),
                        ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(),
                        ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(List.of(receivable(1L, true)));
        Mockito.when(payableRepository.findForReports(
                        ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(),
                        ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(),
                        ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(List.of(payable(2L, true)));

        ReportFileDTO file = service.generate("financial", "xlsx", new ReportFilterDTO());

        Assertions.assertEquals("financial.xlsx", file.getFileName());
        Assertions.assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", file.getContentType());
        Mockito.verify(generator).generate(ArgumentMatchers.eq("Relatório Financeiro"),
                ArgumentMatchers.anyList(), ArgumentMatchers.anyList(), ArgumentMatchers.eq(ReportFormat.XLSX));
    }

    @Test
    void comparisonShouldReturnReceivablesAndPayablesTotals() {
        Mockito.when(receivableRepository.findForReports(
                        ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(),
                        ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(),
                        ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(List.of(receivable(1L, true), receivable(2L, false)));
        Mockito.when(payableRepository.findForReports(
                        ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(),
                        ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(),
                        ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(List.of(payable(3L, true)));

        ReportComparisonDTO comparison = service.comparison(new ReportFilterDTO());

        Assertions.assertEquals(new BigDecimal("200.00"), comparison.getReceivableTotal());
        Assertions.assertEquals(new BigDecimal("50.00"), comparison.getPayableTotal());
        Assertions.assertEquals(new BigDecimal("150.00"), comparison.getBalance());
        Assertions.assertEquals(2, comparison.getReceivableCount());
        Assertions.assertEquals(1, comparison.getPayableCount());
        Assertions.assertEquals(2026, comparison.getYear());
        Assertions.assertEquals(12, comparison.getMonths().size());
        Assertions.assertEquals(new BigDecimal("200.00"), comparison.getMonths().get(6).getReceivableTotal());
        Assertions.assertEquals(new BigDecimal("50.00"), comparison.getMonths().get(6).getPayableTotal());
    }

    private Receivable receivable(Long id, boolean paid) {
        Receivable receivable = new Receivable();
        receivable.setId(id);
        receivable.setDescription("Receivable");
        receivable.setAmount(new BigDecimal("100.00"));
        receivable.setDueDate(LocalDate.of(2026, 7, 1));
        receivable.setPaymentDate(paid ? LocalDate.of(2026, 7, 2) : null);
        receivable.setPaid(paid);
        receivable.setCanceled(false);
        receivable.setRemainingBalance(paid ? BigDecimal.ZERO : new BigDecimal("100.00"));
        return receivable;
    }

    private Payable payable(Long id, boolean paid) {
        Payable payable = new Payable();
        payable.setId(id);
        payable.setDescription("Payable");
        payable.setAmount(new BigDecimal("50.00"));
        payable.setDueDate(LocalDate.of(2026, 7, 1));
        payable.setPaymentDate(paid ? LocalDate.of(2026, 7, 2) : null);
        payable.setPaid(paid);
        payable.setCanceled(false);
        payable.setRemainingBalance(paid ? BigDecimal.ZERO : new BigDecimal("50.00"));
        return payable;
    }
}
