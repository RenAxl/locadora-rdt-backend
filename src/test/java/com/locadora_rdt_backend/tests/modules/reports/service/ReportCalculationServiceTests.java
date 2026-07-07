package com.locadora_rdt_backend.tests.modules.reports.service;

import com.locadora_rdt_backend.modules.customers.model.Customer;
import com.locadora_rdt_backend.modules.employees.model.Employee;
import com.locadora_rdt_backend.modules.financial.payables.model.Payable;
import com.locadora_rdt_backend.modules.financial.receivables.model.Receivable;
import com.locadora_rdt_backend.modules.reports.dto.ReportComparisonDTO;
import com.locadora_rdt_backend.modules.reports.service.ReportCalculationService;
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
        Payable augustPayable = payable("50.00", true);
        augustPayable.setPaymentDate(LocalDate.of(2026, 8, 2));

        Assertions.assertEquals(new BigDecimal("100.00"),
                service.sumPaidReceivablesByMonth(List.of(julyReceivable), 7));
        Assertions.assertEquals(BigDecimal.ZERO,
                service.sumPaidReceivablesByMonth(List.of(julyReceivable), 8));
        Assertions.assertEquals(new BigDecimal("50.00"),
                service.sumPaidPayablesByMonth(List.of(augustPayable), 8));
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
