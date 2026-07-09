package com.locadora_rdt_backend.tests.modules.reports.financialreports.service;

import com.locadora_rdt_backend.modules.customers.model.Customer;
import com.locadora_rdt_backend.modules.employees.model.Employee;
import com.locadora_rdt_backend.modules.financial.payables.model.Payable;
import com.locadora_rdt_backend.modules.financial.receivables.model.Receivable;
import com.locadora_rdt_backend.modules.reports.financialreports.dto.ReportComparisonDTO;
import com.locadora_rdt_backend.modules.reports.financialreports.service.ReportCalculationService;
import com.locadora_rdt_backend.modules.suppliers.model.Supplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

class ReportCalculationServiceTests {

    private final ReportCalculationService service = new ReportCalculationService();

    @Test
    void shouldSumTotalsAndPaidTotals() {
        List<Receivable> receivables = List.of(receivable("100.00", true), receivable(null, false));
        List<Payable> payables = List.of(payable("40.00", true), payable("10.00", false));

        Assertions.assertEquals(new BigDecimal("100.00"), service.sumReceivables(receivables));
        Assertions.assertEquals(new BigDecimal("50.00"), service.sumPayables(payables));
        Assertions.assertEquals(new BigDecimal("100.00"), service.sumPaidReceivables(receivables));
        Assertions.assertEquals(new BigDecimal("40.00"), service.sumPaidPayables(payables));
    }

    @Test
    void shouldSumPaidValuesByPaymentMonth() {
        Receivable julyReceivable = receivable("100.00", true);
        julyReceivable.setPaymentDate(LocalDate.of(2026, 7, 2));
        Receivable unpaidReceivable = receivable("200.00", false);
        unpaidReceivable.setPaymentDate(LocalDate.of(2026, 7, 2));
        Receivable noPaymentDateReceivable = receivable("300.00", true);
        noPaymentDateReceivable.setPaymentDate(null);
        Payable augustPayable = payable("50.00", true);
        augustPayable.setPaymentDate(LocalDate.of(2026, 8, 2));
        Payable unpaidPayable = payable("70.00", false);
        unpaidPayable.setPaymentDate(LocalDate.of(2026, 8, 2));
        Payable noPaymentDatePayable = payable("80.00", true);
        noPaymentDatePayable.setPaymentDate(null);

        Assertions.assertEquals(new BigDecimal("100.00"),
                service.sumPaidReceivablesByMonth(List.of(julyReceivable, unpaidReceivable, noPaymentDateReceivable), 7));
        Assertions.assertEquals(BigDecimal.ZERO,
                service.sumPaidReceivablesByMonth(List.of(julyReceivable), 8));
        Assertions.assertEquals(new BigDecimal("50.00"),
                service.sumPaidPayablesByMonth(List.of(augustPayable, unpaidPayable, noPaymentDatePayable), 8));
    }

    @Test
    void monthlyComparisonShouldUseCreatedDateWhenRequested() {
        Receivable receivable = receivable("100.00", false);
        receivable.setCreatedAt(Instant.parse("2026-03-10T00:00:00Z"));
        Payable payable = payable("50.00", false);
        payable.setCreatedAt(Instant.parse("2026-03-11T00:00:00Z"));

        List<ReportComparisonDTO.ReportComparisonMonthDTO> months =
                service.monthlyComparison(List.of(receivable), List.of(payable), "CREATED_DATE");

        Assertions.assertEquals(new BigDecimal("100.00"), months.get(2).getReceivableTotal());
        Assertions.assertEquals(new BigDecimal("50.00"), months.get(2).getPayableTotal());
    }

    @Test
    void monthlyComparisonShouldUsePaymentDateWhenRequested() {
        Receivable receivable = receivable("100.00", true);
        receivable.setPaymentDate(LocalDate.of(2026, 4, 10));
        Payable payable = payable("50.00", true);
        payable.setPaymentDate(LocalDate.of(2026, 4, 11));

        List<ReportComparisonDTO.ReportComparisonMonthDTO> months =
                service.monthlyComparison(List.of(receivable), List.of(payable), "PAYMENT_DATE");

        Assertions.assertEquals(new BigDecimal("100.00"), months.get(3).getReceivableTotal());
        Assertions.assertEquals(new BigDecimal("50.00"), months.get(3).getPayableTotal());
    }

    @Test
    void monthlyComparisonShouldUseDueDateWhenCreatedAtIsNull() {
        Receivable receivable = receivable("100.00", false);
        receivable.setCreatedAt(null);
        receivable.setDueDate(LocalDate.of(2026, 5, 1));
        Payable payable = payable("50.00", false);
        payable.setCreatedAt(null);
        payable.setDueDate(LocalDate.of(2026, 5, 2));

        List<ReportComparisonDTO.ReportComparisonMonthDTO> months =
                service.monthlyComparison(List.of(receivable), List.of(payable), "CREATED_DATE");

        Assertions.assertEquals(new BigDecimal("100.00"), months.get(4).getReceivableTotal());
        Assertions.assertEquals(new BigDecimal("50.00"), months.get(4).getPayableTotal());
    }

    @Test
    void monthlyComparisonShouldIgnoreItemsWithoutDate() {
        Receivable receivable = receivable("100.00", false);
        receivable.setDueDate(null);
        Payable payable = payable("50.00", false);
        payable.setDueDate(null);

        List<ReportComparisonDTO.ReportComparisonMonthDTO> months =
                service.monthlyComparison(List.of(receivable), List.of(payable), "DUE_DATE");

        Assertions.assertEquals(BigDecimal.ZERO, months.get(0).getReceivableTotal());
        Assertions.assertEquals(BigDecimal.ZERO, months.get(0).getPayableTotal());
    }

    @Test
    void shouldGroupSummaryValues() {
        Receivable receivable = receivable("100.00", true);
        Customer customer = new Customer();
        customer.setName("Cliente A");
        receivable.setCustomer(customer);

        Payable supplierPayable = payable("50.00", true);
        Supplier supplier = new Supplier();
        supplier.setName("Fornecedor A");
        supplierPayable.setSupplier(supplier);

        Payable employeePayable = payable("30.00", false);
        Employee employee = new Employee();
        employee.setName("Funcionário A");
        employeePayable.setEmployee(employee);

        Map<String, ReportCalculationService.SummaryValues> customers =
                service.groupReceivablesByCustomer(List.of(receivable));
        Map<String, ReportCalculationService.SummaryValues> suppliers =
                service.groupPayablesBySupplier(List.of(supplierPayable));
        Map<String, ReportCalculationService.SummaryValues> employees =
                service.groupPayablesByEmployee(List.of(employeePayable));

        Assertions.assertEquals(1, customers.get("Cliente A").getQuantity());
        Assertions.assertEquals(new BigDecimal("100.00"), customers.get("Cliente A").getPaid());
        Assertions.assertEquals(new BigDecimal("50.00"), suppliers.get("Fornecedor A").getTotal());
        Assertions.assertEquals(BigDecimal.ZERO, employees.get("Funcionário A").getPaid());
    }

    @Test
    void shouldGroupDefaultNamesWhenRelationsAreNull() {
        Receivable receivable = receivable("100.00", false);
        Payable supplierPayable = payable("50.00", false);
        Payable employeePayable = payable("30.00", false);

        Map<String, ReportCalculationService.SummaryValues> customers =
                service.groupReceivablesByCustomer(List.of(receivable));
        Map<String, ReportCalculationService.SummaryValues> suppliers =
                service.groupPayablesBySupplier(List.of(supplierPayable));
        Map<String, ReportCalculationService.SummaryValues> employees =
                service.groupPayablesByEmployee(List.of(employeePayable));

        Assertions.assertTrue(customers.containsKey("Sem cliente"));
        Assertions.assertTrue(suppliers.containsKey("Sem fornecedor"));
        Assertions.assertTrue(employees.containsKey("Sem funcionário"));
    }

    private Receivable receivable(String amount, boolean paid) {
        Receivable receivable = new Receivable();
        receivable.setAmount(amount == null ? null : new BigDecimal(amount));
        receivable.setDueDate(LocalDate.of(2026, 7, 1));
        receivable.setPaid(paid);
        return receivable;
    }

    private Payable payable(String amount, boolean paid) {
        Payable payable = new Payable();
        payable.setAmount(amount == null ? null : new BigDecimal(amount));
        payable.setDueDate(LocalDate.of(2026, 7, 1));
        payable.setPaid(paid);
        return payable;
    }
}
