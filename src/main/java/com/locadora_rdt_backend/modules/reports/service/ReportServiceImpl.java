package com.locadora_rdt_backend.modules.reports.service;

import com.locadora_rdt_backend.modules.financial.payables.model.Payable;
import com.locadora_rdt_backend.modules.financial.receivables.model.Receivable;
import com.locadora_rdt_backend.modules.reports.dto.ReportComparisonDTO;
import com.locadora_rdt_backend.modules.reports.dto.ReportFileDTO;
import com.locadora_rdt_backend.modules.reports.dto.ReportFilterDTO;
import com.locadora_rdt_backend.modules.reports.model.ReportFormat;
import com.locadora_rdt_backend.modules.reports.model.ReportType;
import com.locadora_rdt_backend.modules.reports.repository.ReportPayableRepository;
import com.locadora_rdt_backend.modules.reports.repository.ReportReceivableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal FILTER_AMOUNT_DISABLED = BigDecimal.valueOf(-1);
    private static final LocalDate FILTER_DATE_DISABLED = LocalDate.of(1970, 1, 1);
    private static final long FILTER_ID_DISABLED = -1L;

    private final ReportReceivableRepository receivableRepository;
    private final ReportPayableRepository payableRepository;
    private final JasperReportGenerator jasperReportGenerator;
    private final Clock clock;

    public ReportServiceImpl(
            ReportReceivableRepository receivableRepository,
            ReportPayableRepository payableRepository,
            JasperReportGenerator jasperReportGenerator,
            Clock clock
    ) {
        this.receivableRepository = receivableRepository;
        this.payableRepository = payableRepository;
        this.jasperReportGenerator = jasperReportGenerator;
        this.clock = clock;
    }

    @Override
    @Transactional(readOnly = true)
    public ReportFileDTO generate(String reportTypeValue, String formatValue, ReportFilterDTO filters) {
        ReportType reportType = ReportType.from(reportTypeValue);
        ReportFormat format = ReportFormat.from(formatValue);
        ReportData data = buildReportData(reportType, normalize(filters));
        byte[] content = jasperReportGenerator.generate(data.title, data.columns, data.rows, format);
        String fileName = reportType.name().toLowerCase() + "." + format.getExtension();
        return new ReportFileDTO(fileName, format.getContentType(), content);
    }

    @Override
    @Transactional(readOnly = true)
    public ReportComparisonDTO comparison(ReportFilterDTO filters) {
        ReportFilterDTO normalized = normalize(filters);
        int year = normalized.getYear() == null ? LocalDate.now(clock).getYear() : normalized.getYear();
        ReportFilterDTO comparisonFilters = copy(normalized);
        comparisonFilters.setStartDate(LocalDate.of(year, 1, 1));
        comparisonFilters.setEndDate(LocalDate.of(year, 12, 31));

        List<Receivable> receivables = findReceivables(comparisonFilters);
        List<Payable> payables = findPayables(comparisonFilters);
        BigDecimal receivableTotal = sumReceivables(receivables);
        BigDecimal payableTotal = sumPayables(payables);

        return new ReportComparisonDTO(
                receivableTotal,
                payableTotal,
                receivableTotal.subtract(payableTotal),
                receivables.size(),
                payables.size(),
                year,
                monthlyComparison(receivables, payables, comparisonFilters.getPeriodType())
        );
    }

    private ReportData buildReportData(ReportType type, ReportFilterDTO filters) {
        if (type == ReportType.RECEIVABLES) {
            return receivablesReport(filters);
        }

        if (type == ReportType.PAYABLES) {
            return payablesReport(filters);
        }

        if (type == ReportType.FINANCIAL) {
            return financialReport(filters);
        }

        if (type == ReportType.SUMMARY_CUSTOMER) {
            return summaryCustomerReport(filters);
        }

        if (type == ReportType.SUMMARY_SUPPLIER) {
            return summarySupplierReport(filters);
        }

        if (type == ReportType.SUMMARY_EMPLOYEE) {
            return summaryEmployeeReport(filters);
        }

        return annualBalanceReport(filters);
    }

    private ReportData receivablesReport(ReportFilterDTO filters) {
        List<Receivable> items = findReceivables(filters);
        List<String> columns = Arrays.asList("ID", "Descrição", "Cliente", "Vencimento", "Pagamento", "Valor", "Status");
        List<Map<String, ?>> rows = new ArrayList<>();

        for (Receivable item : items) {
            rows.add(row(
                    String.valueOf(item.getId()),
                    text(item.getDescription()),
                    item.getCustomer() == null ? "" : text(item.getCustomer().getName()),
                    date(item.getDueDate()),
                    date(item.getPaymentDate()),
                    money(item.getAmount()),
                    status(item.getPaid(), item.getCanceled(), item.getDueDate(), item.getRemainingBalance(), item.getAmount())
            ));
        }

        addTotalRow(rows, "Total", sumReceivables(items), columns.size());
        return new ReportData("Relatório de Contas a Receber", columns, rows);
    }

    private ReportData payablesReport(ReportFilterDTO filters) {
        List<Payable> items = findPayables(filters);
        List<String> columns = Arrays.asList("ID", "Descrição", "Fornecedor", "Funcionário", "Vencimento", "Pagamento", "Valor", "Status");
        List<Map<String, ?>> rows = new ArrayList<>();

        for (Payable item : items) {
            rows.add(row(
                    String.valueOf(item.getId()),
                    text(item.getDescription()),
                    item.getSupplier() == null ? "" : text(item.getSupplier().getName()),
                    item.getEmployee() == null ? "" : text(item.getEmployee().getName()),
                    date(item.getDueDate()),
                    date(item.getPaymentDate()),
                    money(item.getAmount()),
                    status(item.getPaid(), item.getCanceled(), item.getDueDate(), item.getRemainingBalance(), item.getAmount())
            ));
        }

        addTotalRow(rows, "Total", sumPayables(items), columns.size());
        return new ReportData("Relatório de Contas a Pagar", columns, rows);
    }

    private ReportData financialReport(ReportFilterDTO filters) {
        List<Receivable> receivables = findReceivables(filters);
        List<Payable> payables = findPayables(filters);

        BigDecimal revenue = sumReceivables(receivables);
        BigDecimal expense = sumPayables(payables);
        BigDecimal received = sumPaidReceivables(receivables);
        BigDecimal paid = sumPaidPayables(payables);
        BigDecimal balance = received.subtract(paid);

        List<String> columns = Arrays.asList("Indicador", "Valor");
        List<Map<String, ?>> rows = new ArrayList<>();
        rows.add(row("Receitas", money(revenue)));
        rows.add(row("Despesas", money(expense)));
        rows.add(row("Total recebido", money(received)));
        rows.add(row("Total pago", money(paid)));
        rows.add(row("Saldo", money(balance)));

        return new ReportData("Relatório Financeiro", columns, rows);
    }

    private ReportData summaryCustomerReport(ReportFilterDTO filters) {
        List<Receivable> items = findReceivables(filters);
        Map<String, SummaryValues> grouped = new LinkedHashMap<>();

        for (Receivable item : items) {
            String name = item.getCustomer() == null ? "Sem cliente" : item.getCustomer().getName();
            addSummary(grouped, name, item.getAmount(), Boolean.TRUE.equals(item.getPaid()));
        }

        return summaryReport("Relatório Sintético por Cliente", "Cliente", grouped);
    }

    private ReportData summarySupplierReport(ReportFilterDTO filters) {
        List<Payable> items = findPayables(filters);
        Map<String, SummaryValues> grouped = new LinkedHashMap<>();

        for (Payable item : items) {
            String name = item.getSupplier() == null ? "Sem fornecedor" : item.getSupplier().getName();
            addSummary(grouped, name, item.getAmount(), Boolean.TRUE.equals(item.getPaid()));
        }

        return summaryReport("Relatório Sintético por Fornecedor", "Fornecedor", grouped);
    }

    private ReportData summaryEmployeeReport(ReportFilterDTO filters) {
        List<Payable> items = findPayables(filters);
        Map<String, SummaryValues> grouped = new LinkedHashMap<>();

        for (Payable item : items) {
            String name = item.getEmployee() == null ? "Sem funcionário" : item.getEmployee().getName();
            addSummary(grouped, name, item.getAmount(), Boolean.TRUE.equals(item.getPaid()));
        }

        return summaryReport("Relatório Sintético por Funcionário", "Funcionário", grouped);
    }

    private ReportData annualBalanceReport(ReportFilterDTO filters) {
        int year = filters.getYear() == null ? LocalDate.now(clock).getYear() : filters.getYear();
        ReportFilterDTO annualFilters = copy(filters);
        annualFilters.setStatus("PAID");
        annualFilters.setPeriodType("PAYMENT_DATE");
        annualFilters.setStartDate(LocalDate.of(year, 1, 1));
        annualFilters.setEndDate(LocalDate.of(year, 12, 31));

        List<Receivable> receivables = findReceivables(annualFilters);
        List<Payable> payables = findPayables(annualFilters);
        List<String> columns = Arrays.asList("Mês", "Total recebido", "Total pago", "Saldo");
        List<Map<String, ?>> rows = new ArrayList<>();

        BigDecimal yearReceived = ZERO;
        BigDecimal yearPaid = ZERO;

        for (Month month : Month.values()) {
            BigDecimal received = sumPaidReceivablesByMonth(receivables, month.getValue());
            BigDecimal paid = sumPaidPayablesByMonth(payables, month.getValue());
            yearReceived = yearReceived.add(received);
            yearPaid = yearPaid.add(paid);
            rows.add(row(monthName(month), money(received), money(paid), money(received.subtract(paid))));
        }

        rows.add(row("Total do ano", money(yearReceived), money(yearPaid), money(yearReceived.subtract(yearPaid))));
        return new ReportData("Balanço Anual " + year, columns, rows);
    }

    private List<Receivable> findReceivables(ReportFilterDTO filters) {
        return receivableRepository.findForReports(
                trimToNull(filters.getSearch()),
                dateFilterOrDisabled(filters.getStartDate()),
                dateFilterOrDisabled(filters.getEndDate()),
                filters.getStartDate() != null,
                filters.getEndDate() != null,
                filters.getStatus(),
                filters.getPeriodType(),
                idFilterOrDisabled(filters.getCustomerId()),
                idFilterOrDisabled(filters.getPaymentMethodId()),
                amountFilterOrDisabled(filters.getMinimumAmount()),
                amountFilterOrDisabled(filters.getMaximumAmount())
        );
    }

    private List<Payable> findPayables(ReportFilterDTO filters) {
        return payableRepository.findForReports(
                trimToNull(filters.getSearch()),
                dateFilterOrDisabled(filters.getStartDate()),
                dateFilterOrDisabled(filters.getEndDate()),
                filters.getStartDate() != null,
                filters.getEndDate() != null,
                filters.getStatus(),
                filters.getPeriodType(),
                idFilterOrDisabled(filters.getSupplierId()),
                idFilterOrDisabled(filters.getEmployeeId()),
                idFilterOrDisabled(filters.getPaymentMethodId()),
                amountFilterOrDisabled(filters.getMinimumAmount()),
                amountFilterOrDisabled(filters.getMaximumAmount())
        );
    }

    private ReportFilterDTO normalize(ReportFilterDTO filters) {
        ReportFilterDTO normalized = filters == null ? new ReportFilterDTO() : filters;

        if (!hasText(normalized.getStatus())) {
            normalized.setStatus("ALL");
        } else {
            normalized.setStatus(normalized.getStatus().trim().replace("-", "_").toUpperCase());
        }

        if (!hasText(normalized.getPeriodType())) {
            normalized.setPeriodType("DUE_DATE");
        } else {
            normalized.setPeriodType(normalized.getPeriodType().trim().replace("-", "_").toUpperCase());
        }

        return normalized;
    }

    private ReportFilterDTO copy(ReportFilterDTO source) {
        ReportFilterDTO copy = new ReportFilterDTO();
        copy.setSearch(source.getSearch());
        copy.setStartDate(source.getStartDate());
        copy.setEndDate(source.getEndDate());
        copy.setStatus(source.getStatus());
        copy.setPeriodType(source.getPeriodType());
        copy.setCustomerId(source.getCustomerId());
        copy.setSupplierId(source.getSupplierId());
        copy.setEmployeeId(source.getEmployeeId());
        copy.setPaymentMethodId(source.getPaymentMethodId());
        copy.setMinimumAmount(source.getMinimumAmount());
        copy.setMaximumAmount(source.getMaximumAmount());
        copy.setYear(source.getYear());
        return copy;
    }

    private void addTotalRow(List<Map<String, ?>> rows, String label, BigDecimal total, int columns) {
        List<String> values = new ArrayList<>();

        for (int i = 0; i < columns; i++) {
            values.add("");
        }

        values.set(0, label);
        values.set(columns - 2, money(total));
        rows.add(row(values.toArray(new String[0])));
    }

    private ReportData summaryReport(String title, String firstColumn, Map<String, SummaryValues> grouped) {
        List<String> columns = Arrays.asList(firstColumn, "Quantidade", "Total", "Total pago/recebido", "Em aberto");
        List<Map<String, ?>> rows = new ArrayList<>();

        for (Map.Entry<String, SummaryValues> entry : grouped.entrySet()) {
            SummaryValues values = entry.getValue();
            rows.add(row(entry.getKey(), String.valueOf(values.quantity), money(values.total), money(values.paid),
                    money(values.total.subtract(values.paid))));
        }

        return new ReportData(title, columns, rows);
    }

    private void addSummary(Map<String, SummaryValues> grouped, String name, BigDecimal amount, boolean paid) {
        SummaryValues values = grouped.computeIfAbsent(name, key -> new SummaryValues());
        BigDecimal value = valueOrZero(amount);
        values.quantity++;
        values.total = values.total.add(value);

        if (paid) {
            values.paid = values.paid.add(value);
        }
    }

    private BigDecimal sumReceivables(List<Receivable> items) {
        BigDecimal total = ZERO;

        for (Receivable item : items) {
            total = total.add(valueOrZero(item.getAmount()));
        }

        return total;
    }

    private BigDecimal sumPayables(List<Payable> items) {
        BigDecimal total = ZERO;

        for (Payable item : items) {
            total = total.add(valueOrZero(item.getAmount()));
        }

        return total;
    }

    private BigDecimal sumPaidReceivables(List<Receivable> items) {
        BigDecimal total = ZERO;

        for (Receivable item : items) {
            if (Boolean.TRUE.equals(item.getPaid())) {
                total = total.add(valueOrZero(item.getAmount()));
            }
        }

        return total;
    }

    private BigDecimal sumPaidPayables(List<Payable> items) {
        BigDecimal total = ZERO;

        for (Payable item : items) {
            if (Boolean.TRUE.equals(item.getPaid())) {
                total = total.add(valueOrZero(item.getAmount()));
            }
        }

        return total;
    }

    private BigDecimal sumPaidReceivablesByMonth(List<Receivable> items, int month) {
        BigDecimal total = ZERO;

        for (Receivable item : items) {
            if (Boolean.TRUE.equals(item.getPaid()) && item.getPaymentDate() != null
                    && item.getPaymentDate().getMonthValue() == month) {
                total = total.add(valueOrZero(item.getAmount()));
            }
        }

        return total;
    }

    private BigDecimal sumPaidPayablesByMonth(List<Payable> items, int month) {
        BigDecimal total = ZERO;

        for (Payable item : items) {
            if (Boolean.TRUE.equals(item.getPaid()) && item.getPaymentDate() != null
                    && item.getPaymentDate().getMonthValue() == month) {
                total = total.add(valueOrZero(item.getAmount()));
            }
        }

        return total;
    }

    private List<ReportComparisonDTO.ReportComparisonMonthDTO> monthlyComparison(
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

    private Map<String, ?> row(String... values) {
        Map<String, String> row = new LinkedHashMap<>();

        for (int i = 0; i < values.length; i++) {
            row.put("column" + i, values[i]);
        }

        return row;
    }

    private String status(Boolean paid, Boolean canceled, LocalDate dueDate, BigDecimal remainingBalance, BigDecimal amount) {
        if (Boolean.TRUE.equals(canceled)) {
            return "Cancelada";
        }

        if (Boolean.TRUE.equals(paid)) {
            return "Paga";
        }

        BigDecimal remaining = valueOrZero(remainingBalance);
        BigDecimal total = valueOrZero(amount);

        if (remaining.compareTo(ZERO) > 0 && remaining.compareTo(total) < 0) {
            return "Pago parcialmente";
        }

        if (dueDate != null && dueDate.isBefore(LocalDate.now(clock))) {
            return "Vencida";
        }

        return "Em aberto";
    }

    private String date(LocalDate date) {
        if (date == null) {
            return "";
        }

        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private String money(BigDecimal value) {
        return "R$ " + valueOrZero(value).setScale(2, java.math.RoundingMode.HALF_UP).toPlainString().replace(".", ",");
    }

    private String text(String value) {
        if (value == null) {
            return "";
        }

        return value;
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return value.trim();
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private Long idFilterOrDisabled(Long id) {
        if (id == null || id <= 0) {
            return FILTER_ID_DISABLED;
        }

        return id;
    }

    private BigDecimal amountFilterOrDisabled(BigDecimal amount) {
        if (amount == null) {
            return FILTER_AMOUNT_DISABLED;
        }

        return amount;
    }

    private LocalDate dateFilterOrDisabled(LocalDate date) {
        if (date == null) {
            return FILTER_DATE_DISABLED;
        }

        return date;
    }

    private BigDecimal valueOrZero(BigDecimal value) {
        if (value == null) {
            return ZERO;
        }

        return value;
    }

    private String monthName(Month month) {
        String[] names = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        return names[month.getValue() - 1];
    }

    private String shortMonthName(Month month) {
        String[] names = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun",
                "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
        return names[month.getValue() - 1];
    }

    private static class ReportData {
        private final String title;
        private final List<String> columns;
        private final List<Map<String, ?>> rows;

        private ReportData(String title, List<String> columns, List<Map<String, ?>> rows) {
            this.title = title;
            this.columns = columns;
            this.rows = rows;
        }
    }

    private static class SummaryValues {
        private int quantity;
        private BigDecimal total = ZERO;
        private BigDecimal paid = ZERO;
    }
}
