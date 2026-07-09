package com.locadora_rdt_backend.tests.modules.reports.financialreports.service;

import com.locadora_rdt_backend.modules.financial.payables.model.Payable;
import com.locadora_rdt_backend.modules.financial.receivables.model.Receivable;
import com.locadora_rdt_backend.modules.reports.financialreports.service.ReportCalculationService;
import com.locadora_rdt_backend.modules.reports.financialreports.service.ReportTableService;
import com.locadora_rdt_backend.modules.customers.model.Customer;
import com.locadora_rdt_backend.modules.employees.model.Employee;
import com.locadora_rdt_backend.modules.suppliers.model.Supplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class ReportTableServiceTests {

    private final ReportCalculationService calculationService = new ReportCalculationService();
    private final Clock clock = Clock.fixed(Instant.parse("2026-07-07T00:00:00Z"), ZoneOffset.UTC);
    private final ReportTableService service = new ReportTableService(calculationService, clock);

    @Test
    void receivablesReportShouldCreateRowsAndTotal() throws Exception {
        Object report = service.receivablesReport(List.of(receivable(false, LocalDate.of(2026, 7, 1))));

        Assertions.assertEquals("Relatório de Contas a Receber", title(report));
        Assertions.assertEquals(2, rows(report).size());
        Assertions.assertEquals("Vencida", rows(report).get(0).get("column6"));
        Assertions.assertEquals("Total", rows(report).get(1).get("column0"));
        Assertions.assertEquals("R$ 100,00", rows(report).get(1).get("column5"));
    }

    @Test
    void payablesReportShouldCreateRowsAndTotal() throws Exception {
        Object report = service.payablesReport(List.of(payable(true, LocalDate.of(2026, 7, 1))));

        Assertions.assertEquals("Relatório de Contas a Pagar", title(report));
        Assertions.assertEquals("Paga", rows(report).get(0).get("column7"));
        Assertions.assertEquals("R$ 50,00", rows(report).get(1).get("column6"));
    }

    @Test
    void financialReportShouldCreateIndicatorRows() throws Exception {
        Object report = service.financialReport(
                List.of(receivable(true, LocalDate.of(2026, 7, 1))),
                List.of(payable(true, LocalDate.of(2026, 7, 1)))
        );

        Assertions.assertEquals("Relatório Financeiro", title(report));
        Assertions.assertEquals("Saldo", rows(report).get(4).get("column0"));
        Assertions.assertEquals("R$ 50,00", rows(report).get(4).get("column1"));
    }

    @Test
    void annualBalanceReportShouldCreateTwelveMonthsAndTotal() throws Exception {
        Receivable receivable = receivable(true, LocalDate.of(2026, 7, 2));
        receivable.setPaymentDate(LocalDate.of(2026, 7, 2));
        Payable payable = payable(true, LocalDate.of(2026, 7, 3));
        payable.setPaymentDate(LocalDate.of(2026, 7, 3));

        Object report = service.annualBalanceReport(2026, List.of(receivable), List.of(payable));

        Assertions.assertEquals("Balanço Anual 2026", title(report));
        Assertions.assertEquals(13, rows(report).size());
        Assertions.assertEquals("Julho", rows(report).get(6).get("column0"));
        Assertions.assertEquals("R$ 50,00", rows(report).get(12).get("column3"));
    }

    @Test
    void summaryReportShouldCreateSummaryRows() throws Exception {
        Map<String, ReportCalculationService.SummaryValues> grouped = new LinkedHashMap<>();
        grouped.put("Cliente A", calculationService.groupReceivablesByCustomer(
                List.of(receivable(true, LocalDate.of(2026, 7, 1)))
        ).get("Sem cliente"));

        Object report = service.summaryReport("Resumo", "Cliente", grouped);

        Assertions.assertEquals("Resumo", title(report));
        Assertions.assertEquals("Cliente A", rows(report).get(0).get("column0"));
        Assertions.assertEquals("1", rows(report).get(0).get("column1"));
    }

    @Test
    void receivablesReportShouldUseEmptyCustomerWhenCustomerIsNull() throws Exception {
        Receivable receivable = receivable(false, LocalDate.of(2026, 7, 10));
        receivable.setCustomer(null);

        Object report = service.receivablesReport(List.of(receivable));

        Assertions.assertEquals("", rows(report).get(0).get("column2"));
    }

    @Test
    void payablesReportShouldUseEmptySupplierAndEmployeeWhenTheyAreNull() throws Exception {
        Payable payable = payable(false, LocalDate.of(2026, 7, 10));
        payable.setSupplier(null);
        payable.setEmployee(null);

        Object report = service.payablesReport(List.of(payable));

        Assertions.assertEquals("", rows(report).get(0).get("column2"));
        Assertions.assertEquals("", rows(report).get(0).get("column3"));
    }

    @Test
    void payablesReportShouldShowSupplierWhenSupplierExists() throws Exception {
        Payable payable = payable(false, LocalDate.of(2026, 7, 10));
        Supplier supplier = new Supplier();
        supplier.setName("Fornecedor A");
        payable.setSupplier(supplier);

        Object report = service.payablesReport(List.of(payable));

        Assertions.assertEquals("Fornecedor A", rows(report).get(0).get("column2"));
    }

    @Test
    void receivablesReportShouldShowCustomerWhenCustomerExists() throws Exception {
        Receivable receivable = receivable(false, LocalDate.of(2026, 7, 10));
        Customer customer = new Customer();
        customer.setName("Cliente A");
        receivable.setCustomer(customer);

        Object report = service.receivablesReport(List.of(receivable));

        Assertions.assertEquals("Cliente A", rows(report).get(0).get("column2"));
    }

    @Test
    void payablesReportShouldShowEmployeeWhenEmployeeExists() throws Exception {
        Payable payable = payable(false, LocalDate.of(2026, 7, 10));
        Employee employee = new Employee();
        employee.setName("Funcionário A");
        payable.setEmployee(employee);

        Object report = service.payablesReport(List.of(payable));

        Assertions.assertEquals("Funcionário A", rows(report).get(0).get("column3"));
    }

    @Test
    void reportsShouldShowCanceledPartialAndOpenStatuses() throws Exception {
        Receivable canceled = receivable(false, LocalDate.of(2026, 7, 10));
        canceled.setCanceled(true);
        Receivable partial = receivable(false, LocalDate.of(2026, 7, 10));
        partial.setRemainingBalance(new BigDecimal("50.00"));
        Receivable open = receivable(false, LocalDate.of(2026, 7, 10));
        open.setRemainingBalance(new BigDecimal("100.00"));

        Object report = service.receivablesReport(List.of(canceled, partial, open));

        Assertions.assertEquals("Cancelada", rows(report).get(0).get("column6"));
        Assertions.assertEquals("Pago parcialmente", rows(report).get(1).get("column6"));
        Assertions.assertEquals("Em aberto", rows(report).get(2).get("column6"));
    }

    @Test
    void reportsShouldUseEmptyTextZeroMoneyAndEmptyDateWhenValuesAreNull() throws Exception {
        Receivable receivable = receivable(false, null);
        receivable.setDescription(null);
        receivable.setAmount(null);
        receivable.setRemainingBalance(null);

        Object report = service.receivablesReport(List.of(receivable));

        Assertions.assertEquals("", rows(report).get(0).get("column1"));
        Assertions.assertEquals("", rows(report).get(0).get("column3"));
        Assertions.assertEquals("R$ 0,00", rows(report).get(0).get("column5"));
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, ?>> rows(Object report) throws Exception {
        Method method = report.getClass().getDeclaredMethod("getRows");
        method.setAccessible(true);
        return (List<Map<String, ?>>) method.invoke(report);
    }

    private String title(Object report) throws Exception {
        Method method = report.getClass().getDeclaredMethod("getTitle");
        method.setAccessible(true);
        return (String) method.invoke(report);
    }

    private Receivable receivable(boolean paid, LocalDate dueDate) {
        Receivable receivable = new Receivable();
        receivable.setId(1L);
        receivable.setDescription("Conta");
        receivable.setAmount(new BigDecimal("100.00"));
        receivable.setDueDate(dueDate);
        receivable.setPaid(paid);
        receivable.setCanceled(false);
        receivable.setRemainingBalance(paid ? BigDecimal.ZERO : new BigDecimal("100.00"));
        return receivable;
    }

    private Payable payable(boolean paid, LocalDate dueDate) {
        Payable payable = new Payable();
        payable.setId(1L);
        payable.setDescription("Conta");
        payable.setAmount(new BigDecimal("50.00"));
        payable.setDueDate(dueDate);
        payable.setPaid(paid);
        payable.setCanceled(false);
        payable.setRemainingBalance(paid ? BigDecimal.ZERO : new BigDecimal("50.00"));
        return payable;
    }
}
