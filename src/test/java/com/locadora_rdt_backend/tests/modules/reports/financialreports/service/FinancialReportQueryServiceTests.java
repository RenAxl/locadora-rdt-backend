package com.locadora_rdt_backend.tests.modules.reports.financialreports.service;

import com.locadora_rdt_backend.modules.financial.payables.model.Payable;
import com.locadora_rdt_backend.modules.financial.receivables.model.Receivable;
import com.locadora_rdt_backend.modules.reports.financialreports.dto.FinancialReportFilterDTO;
import com.locadora_rdt_backend.modules.reports.financialreports.repository.FinancialReportPayableRepository;
import com.locadora_rdt_backend.modules.reports.financialreports.repository.FinancialReportReceivableRepository;
import com.locadora_rdt_backend.modules.reports.financialreports.service.FinancialReportQueryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

class FinancialReportQueryServiceTests {

    private FinancialReportReceivableRepository receivableRepository;
    private FinancialReportPayableRepository payableRepository;
    private FinancialReportQueryService service;

    @BeforeEach
    void setup() {
        receivableRepository = Mockito.mock(FinancialReportReceivableRepository.class);
        payableRepository = Mockito.mock(FinancialReportPayableRepository.class);
        service = new FinancialReportQueryService(receivableRepository, payableRepository);
    }

    @Test
    void normalizeShouldCreateDefaultFiltersWhenNull() {
        FinancialReportFilterDTO filters = service.normalize(null);

        Assertions.assertEquals("ALL", filters.getStatus());
        Assertions.assertEquals("DUE_DATE", filters.getPeriodType());
    }

    @Test
    void normalizeShouldFormatStatusAndPeriodType() {
        FinancialReportFilterDTO filters = new FinancialReportFilterDTO();
        filters.setStatus(" paid ");
        filters.setPeriodType("payment-date");

        FinancialReportFilterDTO normalized = service.normalize(filters);

        Assertions.assertSame(filters, normalized);
        Assertions.assertEquals("PAID", normalized.getStatus());
        Assertions.assertEquals("PAYMENT_DATE", normalized.getPeriodType());
    }

    @Test
    void normalizeShouldUseDefaultsWhenValuesAreBlank() {
        FinancialReportFilterDTO filters = new FinancialReportFilterDTO();
        filters.setStatus(" ");
        filters.setPeriodType(" ");

        FinancialReportFilterDTO normalized = service.normalize(filters);

        Assertions.assertEquals("ALL", normalized.getStatus());
        Assertions.assertEquals("DUE_DATE", normalized.getPeriodType());
    }

    @Test
    void copyShouldCopyAllFields() {
        FinancialReportFilterDTO filters = new FinancialReportFilterDTO();
        filters.setSearch("teste");
        filters.setStartDate(LocalDate.of(2026, 1, 1));
        filters.setEndDate(LocalDate.of(2026, 12, 31));
        filters.setStatus("PAID");
        filters.setPeriodType("PAYMENT_DATE");
        filters.setCustomerId(1L);
        filters.setSupplierId(2L);
        filters.setEmployeeId(3L);
        filters.setPaymentMethodId(4L);
        filters.setMinimumAmount(new BigDecimal("10.00"));
        filters.setMaximumAmount(new BigDecimal("100.00"));
        filters.setYear(2026);

        FinancialReportFilterDTO copy = service.copy(filters);

        Assertions.assertNotSame(filters, copy);
        Assertions.assertEquals(filters.getSearch(), copy.getSearch());
        Assertions.assertEquals(filters.getStartDate(), copy.getStartDate());
        Assertions.assertEquals(filters.getEndDate(), copy.getEndDate());
        Assertions.assertEquals(filters.getStatus(), copy.getStatus());
        Assertions.assertEquals(filters.getPeriodType(), copy.getPeriodType());
        Assertions.assertEquals(filters.getCustomerId(), copy.getCustomerId());
        Assertions.assertEquals(filters.getSupplierId(), copy.getSupplierId());
        Assertions.assertEquals(filters.getEmployeeId(), copy.getEmployeeId());
        Assertions.assertEquals(filters.getPaymentMethodId(), copy.getPaymentMethodId());
        Assertions.assertEquals(filters.getMinimumAmount(), copy.getMinimumAmount());
        Assertions.assertEquals(filters.getMaximumAmount(), copy.getMaximumAmount());
        Assertions.assertEquals(filters.getYear(), copy.getYear());
    }

    @Test
    void findReceivablesShouldCallRepositoryWithFilterValues() {
        FinancialReportFilterDTO filters = new FinancialReportFilterDTO();
        filters.setSearch(" teste ");
        filters.setStartDate(LocalDate.of(2026, 1, 1));
        filters.setEndDate(LocalDate.of(2026, 1, 31));
        filters.setStatus("PAID");
        filters.setPeriodType("PAYMENT_DATE");
        filters.setCustomerId(10L);
        filters.setPaymentMethodId(20L);
        filters.setMinimumAmount(new BigDecimal("30.00"));
        filters.setMaximumAmount(new BigDecimal("40.00"));
        List<Receivable> expected = List.of(new Receivable());
        Mockito.when(receivableRepository.findForReports(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean(),
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()
        )).thenReturn(expected);

        List<Receivable> result = service.findReceivables(filters);

        Assertions.assertSame(expected, result);
        Mockito.verify(receivableRepository).findForReports(
                "teste",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31),
                true,
                true,
                "PAID",
                "PAYMENT_DATE",
                10L,
                20L,
                new BigDecimal("30.00"),
                new BigDecimal("40.00")
        );
    }

    @Test
    void findPayablesShouldUseDisabledValuesWhenFiltersAreEmpty() {
        FinancialReportFilterDTO filters = new FinancialReportFilterDTO();
        filters.setStatus("ALL");
        filters.setPeriodType("DUE_DATE");
        List<Payable> expected = List.of(new Payable());
        Mockito.when(payableRepository.findForReports(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean(),
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()
        )).thenReturn(expected);

        List<Payable> result = service.findPayables(filters);

        Assertions.assertSame(expected, result);
        Mockito.verify(payableRepository).findForReports(
                null,
                LocalDate.of(1970, 1, 1),
                LocalDate.of(1970, 1, 1),
                false,
                false,
                "ALL",
                "DUE_DATE",
                -1L,
                -1L,
                -1L,
                new BigDecimal("-1"),
                new BigDecimal("-1")
        );
    }

    @Test
    void findReceivablesShouldUseDisabledValuesWhenFiltersAreEmpty() {
        FinancialReportFilterDTO filters = new FinancialReportFilterDTO();
        filters.setSearch(" ");
        filters.setStatus("ALL");
        filters.setPeriodType("DUE_DATE");
        filters.setCustomerId(0L);
        filters.setPaymentMethodId(0L);
        List<Receivable> expected = List.of(new Receivable());
        Mockito.when(receivableRepository.findForReports(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean(),
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()
        )).thenReturn(expected);

        List<Receivable> result = service.findReceivables(filters);

        Assertions.assertSame(expected, result);
        Mockito.verify(receivableRepository).findForReports(
                null,
                LocalDate.of(1970, 1, 1),
                LocalDate.of(1970, 1, 1),
                false,
                false,
                "ALL",
                "DUE_DATE",
                -1L,
                -1L,
                new BigDecimal("-1"),
                new BigDecimal("-1")
        );
    }
}
