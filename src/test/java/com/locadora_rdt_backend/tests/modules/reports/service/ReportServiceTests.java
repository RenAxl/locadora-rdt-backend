package com.locadora_rdt_backend.tests.modules.reports.service;

import com.locadora_rdt_backend.modules.financial.payables.model.Payable;
import com.locadora_rdt_backend.modules.financial.payables.repository.PayableRepository;
import com.locadora_rdt_backend.modules.financial.receivables.model.Receivable;
import com.locadora_rdt_backend.modules.financial.receivables.repository.ReceivableRepository;
import com.locadora_rdt_backend.modules.reports.dto.ReportFileDTO;
import com.locadora_rdt_backend.modules.reports.dto.ReportFilterDTO;
import com.locadora_rdt_backend.modules.reports.model.ReportFormat;
import com.locadora_rdt_backend.modules.reports.service.JasperReportGenerator;
import com.locadora_rdt_backend.modules.reports.service.ReportServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

class ReportServiceTests {

    private ReceivableRepository receivableRepository;
    private PayableRepository payableRepository;
    private JasperReportGenerator generator;
    private ReportServiceImpl service;

    @BeforeEach
    void setup() {
        receivableRepository = Mockito.mock(ReceivableRepository.class);
        payableRepository = Mockito.mock(PayableRepository.class);
        generator = Mockito.mock(JasperReportGenerator.class);
        Clock clock = Clock.fixed(Instant.parse("2026-07-07T00:00:00Z"), ZoneOffset.UTC);
        service = new ReportServiceImpl(receivableRepository, payableRepository, generator, clock);
        Mockito.when(generator.generate(ArgumentMatchers.anyString(), ArgumentMatchers.anyList(),
                        ArgumentMatchers.anyList(), ArgumentMatchers.any(ReportFormat.class)))
                .thenReturn(new byte[]{1, 2, 3});
    }

    @Test
    void generateReceivablesShouldReturnPdfFile() {
        Mockito.when(receivableRepository.findAll(ArgumentMatchers.<Specification<Receivable>>any()))
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
        Mockito.when(receivableRepository.findAll(ArgumentMatchers.<Specification<Receivable>>any()))
                .thenReturn(List.of(receivable(1L, true)));
        Mockito.when(payableRepository.findAll(ArgumentMatchers.<Specification<Payable>>any()))
                .thenReturn(List.of(payable(2L, true)));

        ReportFileDTO file = service.generate("financial", "xlsx", new ReportFilterDTO());

        Assertions.assertEquals("financial.xlsx", file.getFileName());
        Assertions.assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", file.getContentType());
        Mockito.verify(generator).generate(ArgumentMatchers.eq("Relatório Financeiro"),
                ArgumentMatchers.anyList(), ArgumentMatchers.anyList(), ArgumentMatchers.eq(ReportFormat.XLSX));
    }

    @Test
    void voucherShouldReturnReceivableVoucher() {
        Mockito.when(receivableRepository.findById(1L)).thenReturn(Optional.of(receivable(1L, true)));

        ReportFileDTO file = service.voucher("receivable", 1L, "pdf");

        Assertions.assertEquals("comprovante-receivable-1.pdf", file.getFileName());
        Mockito.verify(generator).generate(ArgumentMatchers.eq("Comprovante de Recebimento"),
                ArgumentMatchers.anyList(), ArgumentMatchers.anyList(), ArgumentMatchers.eq(ReportFormat.PDF));
    }

    @Test
    void voucherShouldThrowWhenAccountIsOpen() {
        Mockito.when(payableRepository.findById(2L)).thenReturn(Optional.of(payable(2L, false)));

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.voucher("payable", 2L, "pdf"));
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
