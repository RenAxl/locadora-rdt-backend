package com.locadora_rdt_backend.tests.modules.reports.financialreports.service;

import com.locadora_rdt_backend.modules.organization.customers.model.Customer;
import com.locadora_rdt_backend.modules.organization.employees.model.Employee;
import com.locadora_rdt_backend.modules.financial.payables.model.Payable;
import com.locadora_rdt_backend.modules.financial.receivables.model.Receivable;
import com.locadora_rdt_backend.modules.reports.financialreports.dto.FinancialReportComparisonDTO;
import com.locadora_rdt_backend.shared.reports.ReportFileDTO;
import com.locadora_rdt_backend.modules.reports.financialreports.dto.FinancialReportFilterDTO;
import com.locadora_rdt_backend.shared.reports.ReportFormat;
import com.locadora_rdt_backend.modules.reports.financialreports.repository.FinancialReportPayableRepository;
import com.locadora_rdt_backend.modules.reports.financialreports.repository.FinancialReportReceivableRepository;
import com.locadora_rdt_backend.shared.reports.JasperReportGenerator;
import com.locadora_rdt_backend.modules.reports.financialreports.service.FinancialReportCalculationService;
import com.locadora_rdt_backend.modules.reports.financialreports.service.FinancialReportQueryService;
import com.locadora_rdt_backend.modules.reports.financialreports.service.FinancialReportServiceImpl;
import com.locadora_rdt_backend.modules.reports.financialreports.service.FinancialReportTableService;
import com.locadora_rdt_backend.modules.organization.suppliers.model.Supplier;
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

class FinancialReportServiceTests {

    private FinancialReportReceivableRepository receivableRepository;
    private FinancialReportPayableRepository payableRepository;
    private JasperReportGenerator generator;
    private FinancialReportServiceImpl service;

    @BeforeEach
    void setup() {
        receivableRepository = Mockito.mock(FinancialReportReceivableRepository.class);
        payableRepository = Mockito.mock(FinancialReportPayableRepository.class);
        generator = Mockito.mock(JasperReportGenerator.class);
        Clock clock = Clock.fixed(Instant.parse("2026-07-07T00:00:00Z"), ZoneOffset.UTC);
        FinancialReportQueryService queryService = new FinancialReportQueryService(receivableRepository, payableRepository);
        FinancialReportCalculationService calculationService = new FinancialReportCalculationService();
        FinancialReportTableService tableService = new FinancialReportTableService(calculationService, clock);
        service = new FinancialReportServiceImpl(queryService, calculationService, tableService, generator, clock);
        Mockito.when(generator.generate(ArgumentMatchers.anyString(), ArgumentMatchers.anyList(),
                        ArgumentMatchers.anyList(), ArgumentMatchers.any(ReportFormat.class)))
                .thenReturn(new byte[]{1, 2, 3});
    }

    @Test
    void generateReceivablesShouldReturnPdfFile() {
        mockReceivables(List.of(receivable(1L, true)));

        FinancialReportFilterDTO filters = new FinancialReportFilterDTO();
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
        mockReceivables(List.of(receivable(1L, true)));
        mockPayables(List.of(payable(2L, true)));

        ReportFileDTO file = service.generate("financial", "xlsx", new FinancialReportFilterDTO());

        Assertions.assertEquals("financial.xlsx", file.getFileName());
        Assertions.assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", file.getContentType());
        Mockito.verify(generator).generate(ArgumentMatchers.eq("Relatório Financeiro"),
                ArgumentMatchers.anyList(), ArgumentMatchers.anyList(), ArgumentMatchers.eq(ReportFormat.XLSX));
    }

    @Test
    void comparisonShouldReturnReceivablesAndPayablesTotals() {
        mockReceivables(List.of(receivable(1L, true), receivable(2L, false)));
        mockPayables(List.of(payable(3L, true)));

        FinancialReportComparisonDTO comparison = service.comparison(new FinancialReportFilterDTO());

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

    @Test
    void comparisonShouldUseYearFromFilters() {
        FinancialReportFilterDTO filters = new FinancialReportFilterDTO();
        filters.setYear(2025);
        mockReceivables(List.of(receivable(1L, true)));
        mockPayables(List.of(payable(3L, true)));

        FinancialReportComparisonDTO comparison = service.comparison(filters);

        Assertions.assertEquals(2025, comparison.getYear());
    }

    @Test
    void generatePayablesShouldReturnReport() {
        mockPayables(List.of(payable(2L, false)));

        ReportFileDTO file = service.generate("payables", "pdf", new FinancialReportFilterDTO());

        Assertions.assertEquals("payables.pdf", file.getFileName());
        Mockito.verify(generator).generate(ArgumentMatchers.eq("Relatório de Contas a Pagar"),
                ArgumentMatchers.anyList(), ArgumentMatchers.anyList(), ArgumentMatchers.eq(ReportFormat.PDF));
    }

    @Test
    void generateSummaryReportsShouldGroupValues() {
        mockReceivables(List.of(receivableWithCustomer("Cliente A", true), receivableWithCustomer("Cliente A", false)));
        mockPayables(List.of(payableWithSupplierAndEmployee("Fornecedor A", "Funcionário A", true),
                payableWithSupplierAndEmployee("Fornecedor B", "Funcionário A", false)));

        service.generate("summary-customer", "pdf", new FinancialReportFilterDTO());
        service.generate("summary-supplier", "pdf", new FinancialReportFilterDTO());
        service.generate("summary-employee", "pdf", new FinancialReportFilterDTO());

        Mockito.verify(generator).generate(ArgumentMatchers.eq("Relatório Sintético por Cliente"),
                ArgumentMatchers.anyList(), ArgumentMatchers.anyList(), ArgumentMatchers.eq(ReportFormat.PDF));
        Mockito.verify(generator).generate(ArgumentMatchers.eq("Relatório Sintético por Fornecedor"),
                ArgumentMatchers.anyList(), ArgumentMatchers.anyList(), ArgumentMatchers.eq(ReportFormat.PDF));
        Mockito.verify(generator).generate(ArgumentMatchers.eq("Relatório Sintético por Funcionário"),
                ArgumentMatchers.anyList(), ArgumentMatchers.anyList(), ArgumentMatchers.eq(ReportFormat.PDF));
    }

    @Test
    void generateAnnualBalanceShouldReturnMonthlyRows() {
        FinancialReportFilterDTO filters = new FinancialReportFilterDTO();
        filters.setYear(2026);
        mockReceivables(List.of(receivable(1L, true)));
        mockPayables(List.of(payable(2L, true)));

        ReportFileDTO file = service.generate("annual-balance", "xlsx", filters);

        Assertions.assertEquals("annual_balance.xlsx", file.getFileName());
        Mockito.verify(generator).generate(ArgumentMatchers.eq("Balanço Anual 2026"),
                ArgumentMatchers.anyList(), ArgumentMatchers.anyList(), ArgumentMatchers.eq(ReportFormat.XLSX));
    }

    @Test
    void generateAnnualBalanceShouldUseCurrentYearWhenYearIsNull() {
        mockReceivables(List.of(receivable(1L, true)));
        mockPayables(List.of(payable(2L, true)));

        ReportFileDTO file = service.generate("annual-balance", "pdf", new FinancialReportFilterDTO());

        Assertions.assertEquals("annual_balance.pdf", file.getFileName());
        Mockito.verify(generator).generate(ArgumentMatchers.eq("Balanço Anual 2026"),
                ArgumentMatchers.anyList(), ArgumentMatchers.anyList(), ArgumentMatchers.eq(ReportFormat.PDF));
    }

    @Test
    void generateShouldRejectInvalidValues() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.generate(null, "pdf", new FinancialReportFilterDTO()));
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.generate("receivables", null, new FinancialReportFilterDTO()));
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.generate("invalid", "pdf", new FinancialReportFilterDTO()));
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.generate("receivables", "doc", new FinancialReportFilterDTO()));
    }

    private void mockReceivables(List<Receivable> receivables) {
        Mockito.when(receivableRepository.findForReports(
                        ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(),
                        ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(),
                        ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(receivables);
    }

    private void mockPayables(List<Payable> payables) {
        Mockito.when(payableRepository.findForReports(
                        ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(),
                        ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(),
                        ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(payables);
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

    private Receivable receivableWithCustomer(String name, boolean paid) {
        Receivable receivable = receivable(1L, paid);
        Customer customer = new Customer();
        customer.setName(name);
        receivable.setCustomer(customer);
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

    private Payable payableWithSupplierAndEmployee(String supplierName, String employeeName, boolean paid) {
        Payable payable = payable(2L, paid);
        Supplier supplier = new Supplier();
        supplier.setName(supplierName);
        Employee employee = new Employee();
        employee.setName(employeeName);
        payable.setSupplier(supplier);
        payable.setEmployee(employee);
        return payable;
    }
}
