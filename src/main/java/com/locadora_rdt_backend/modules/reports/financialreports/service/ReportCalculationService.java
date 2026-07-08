package com.locadora_rdt_backend.modules.reports.financialreports.service;

import com.locadora_rdt_backend.modules.financial.payables.model.Payable;
import com.locadora_rdt_backend.modules.financial.receivables.model.Receivable;
import com.locadora_rdt_backend.modules.reports.financialreports.dto.ReportComparisonDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportCalculationService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    public BigDecimal sumReceivables(List<Receivable> items) {
        BigDecimal total = ZERO;

        for (Receivable item : items) {
            total = total.add(valueOrZero(item.getAmount()));
        }

        return total;
    }

    public BigDecimal sumPayables(List<Payable> items) {
        BigDecimal total = ZERO;

        for (Payable item : items) {
            total = total.add(valueOrZero(item.getAmount()));
        }

        return total;
    }

    public BigDecimal sumPaidReceivables(List<Receivable> items) {
        BigDecimal total = ZERO;

        for (Receivable item : items) {
            if (Boolean.TRUE.equals(item.getPaid())) {
                total = total.add(valueOrZero(item.getAmount()));
            }
        }

        return total;
    }

    public BigDecimal sumPaidPayables(List<Payable> items) {
        BigDecimal total = ZERO;

        for (Payable item : items) {
            if (Boolean.TRUE.equals(item.getPaid())) {
                total = total.add(valueOrZero(item.getAmount()));
            }
        }

        return total;
    }

    public BigDecimal sumPaidReceivablesByMonth(List<Receivable> items, int month) {
        BigDecimal total = ZERO;

        for (Receivable item : items) {
            if (Boolean.TRUE.equals(item.getPaid()) && item.getPaymentDate() != null
                    && item.getPaymentDate().getMonthValue() == month) {
                total = total.add(valueOrZero(item.getAmount()));
            }
        }

        return total;
    }

    public BigDecimal sumPaidPayablesByMonth(List<Payable> items, int month) {
        BigDecimal total = ZERO;

        for (Payable item : items) {
            if (Boolean.TRUE.equals(item.getPaid()) && item.getPaymentDate() != null
                    && item.getPaymentDate().getMonthValue() == month) {
                total = total.add(valueOrZero(item.getAmount()));
            }
        }

        return total;
    }

    public List<ReportComparisonDTO.ReportComparisonMonthDTO> monthlyComparison(
            List<Receivable> receivables,
            List<Payable> payables,
            String periodType
    ) {
        List<ReportComparisonDTO.ReportComparisonMonthDTO> months = new ArrayList<>();

        for (Month month : Month.values()) {
            months.add(new ReportComparisonDTO.ReportComparisonMonthDTO(
                    month.getValue(),
                    shortMonthName(month),
                    sumReceivablesByMonth(receivables, month.getValue(), periodType),
                    sumPayablesByMonth(payables, month.getValue(), periodType)
            ));
        }

        return months;
    }

    public Map<String, SummaryValues> groupReceivablesByCustomer(List<Receivable> items) {
        Map<String, SummaryValues> grouped = new LinkedHashMap<>();

        for (Receivable item : items) {
            String name = item.getCustomer() == null ? "Sem cliente" : item.getCustomer().getName();
            addSummary(grouped, name, item.getAmount(), Boolean.TRUE.equals(item.getPaid()));
        }

        return grouped;
    }

    public Map<String, SummaryValues> groupPayablesBySupplier(List<Payable> items) {
        Map<String, SummaryValues> grouped = new LinkedHashMap<>();

        for (Payable item : items) {
            String name = item.getSupplier() == null ? "Sem fornecedor" : item.getSupplier().getName();
            addSummary(grouped, name, item.getAmount(), Boolean.TRUE.equals(item.getPaid()));
        }

        return grouped;
    }

    public Map<String, SummaryValues> groupPayablesByEmployee(List<Payable> items) {
        Map<String, SummaryValues> grouped = new LinkedHashMap<>();

        for (Payable item : items) {
            String name = item.getEmployee() == null ? "Sem funcionário" : item.getEmployee().getName();
            addSummary(grouped, name, item.getAmount(), Boolean.TRUE.equals(item.getPaid()));
        }

        return grouped;
    }

    private void addSummary(Map<String, SummaryValues> grouped, String name, BigDecimal amount, boolean paid) {
        SummaryValues values = grouped.computeIfAbsent(name, key -> new SummaryValues());
        BigDecimal value = valueOrZero(amount);
        values.addQuantity();
        values.addTotal(value);

        if (paid) {
            values.addPaid(value);
        }
    }

    private BigDecimal sumReceivablesByMonth(List<Receivable> items, int month, String periodType) {
        BigDecimal total = ZERO;

        for (Receivable item : items) {
            LocalDate date = comparisonDate(item, periodType);
            if (date != null && date.getMonthValue() == month) {
                total = total.add(valueOrZero(item.getAmount()));
            }
        }

        return total;
    }

    private BigDecimal sumPayablesByMonth(List<Payable> items, int month, String periodType) {
        BigDecimal total = ZERO;

        for (Payable item : items) {
            LocalDate date = comparisonDate(item, periodType);
            if (date != null && date.getMonthValue() == month) {
                total = total.add(valueOrZero(item.getAmount()));
            }
        }

        return total;
    }

    private LocalDate comparisonDate(Receivable item, String periodType) {
        if ("PAYMENT_DATE".equals(periodType)) {
            return item.getPaymentDate();
        }

        if ("CREATED_DATE".equals(periodType) && item.getCreatedAt() != null) {
            return LocalDate.ofInstant(item.getCreatedAt(), ZoneOffset.UTC);
        }

        return item.getDueDate();
    }

    private LocalDate comparisonDate(Payable item, String periodType) {
        if ("PAYMENT_DATE".equals(periodType)) {
            return item.getPaymentDate();
        }

        if ("CREATED_DATE".equals(periodType) && item.getCreatedAt() != null) {
            return LocalDate.ofInstant(item.getCreatedAt(), ZoneOffset.UTC);
        }

        return item.getDueDate();
    }

    private BigDecimal valueOrZero(BigDecimal value) {
        if (value == null) {
            return ZERO;
        }

        return value;
    }

    private String shortMonthName(Month month) {
        String[] names = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun",
                "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
        return names[month.getValue() - 1];
    }

    public static class SummaryValues {

        private int quantity;
        private BigDecimal total = ZERO;
        private BigDecimal paid = ZERO;

        public int getQuantity() {
            return quantity;
        }

        public BigDecimal getTotal() {
            return total;
        }

        public BigDecimal getPaid() {
            return paid;
        }

        private void addQuantity() {
            quantity++;
        }

        private void addTotal(BigDecimal value) {
            total = total.add(value);
        }

        private void addPaid(BigDecimal value) {
            paid = paid.add(value);
        }
    }
}
