package com.locadora_rdt_backend.modules.reports.service;

import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.financial.payables.model.Payable;
import com.locadora_rdt_backend.modules.financial.payables.repository.PayableRepository;
import com.locadora_rdt_backend.modules.financial.receivables.model.Receivable;
import com.locadora_rdt_backend.modules.financial.receivables.repository.ReceivableRepository;
import com.locadora_rdt_backend.modules.reports.dto.ReportFileDTO;
import com.locadora_rdt_backend.modules.reports.dto.ReportFilterDTO;
import com.locadora_rdt_backend.modules.reports.model.ReportFormat;
import com.locadora_rdt_backend.modules.reports.model.ReportType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
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

    private final ReceivableRepository receivableRepository;
    private final PayableRepository payableRepository;
    private final JasperReportGenerator jasperReportGenerator;
    private final Clock clock;

    public ReportServiceImpl(
            ReceivableRepository receivableRepository,
            PayableRepository payableRepository,
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
    public ReportFileDTO voucher(String accountType, Long accountId, String formatValue) {
        ReportFormat format = ReportFormat.from(formatValue);
        ReportData data;

        if ("payable".equalsIgnoreCase(accountType)) {
            data = payableVoucher(accountId);
        } else if ("receivable".equalsIgnoreCase(accountType)) {
            data = receivableVoucher(accountId);
        } else {
            throw new IllegalArgumentException("Tipo de conta inválido.");
        }

        byte[] content = jasperReportGenerator.generate(data.title, data.columns, data.rows, format);
        return new ReportFileDTO("comprovante-" + accountType + "-" + accountId + "." + format.getExtension(),
                format.getContentType(), content);
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
        List<Receivable> items = receivableRepository.findAll(receivableSpec(filters));
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
        List<Payable> items = payableRepository.findAll(payableSpec(filters));
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
        List<Receivable> receivables = receivableRepository.findAll(receivableSpec(filters));
        List<Payable> payables = payableRepository.findAll(payableSpec(filters));

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
        List<Receivable> items = receivableRepository.findAll(receivableSpec(filters));
        Map<String, SummaryValues> grouped = new LinkedHashMap<>();

        for (Receivable item : items) {
            String name = item.getCustomer() == null ? "Sem cliente" : item.getCustomer().getName();
            addSummary(grouped, name, item.getAmount(), Boolean.TRUE.equals(item.getPaid()));
        }

        return summaryReport("Relatório Sintético por Cliente", "Cliente", grouped);
    }

    private ReportData summarySupplierReport(ReportFilterDTO filters) {
        List<Payable> items = payableRepository.findAll(payableSpec(filters));
        Map<String, SummaryValues> grouped = new LinkedHashMap<>();

        for (Payable item : items) {
            String name = item.getSupplier() == null ? "Sem fornecedor" : item.getSupplier().getName();
            addSummary(grouped, name, item.getAmount(), Boolean.TRUE.equals(item.getPaid()));
        }

        return summaryReport("Relatório Sintético por Fornecedor", "Fornecedor", grouped);
    }

    private ReportData summaryEmployeeReport(ReportFilterDTO filters) {
        List<Payable> items = payableRepository.findAll(payableSpec(filters));
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

        List<Receivable> receivables = receivableRepository.findAll(receivableSpec(annualFilters));
        List<Payable> payables = payableRepository.findAll(payableSpec(annualFilters));
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

    private ReportData receivableVoucher(Long id) {
        Receivable item = receivableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta a receber não encontrada. Id: " + id));

        validatePaid(item.getPaid());

        List<String> columns = Arrays.asList("Campo", "Valor");
        List<Map<String, ?>> rows = new ArrayList<>();
        rows.add(row("Conta", String.valueOf(item.getId())));
        rows.add(row("Descrição", text(item.getDescription())));
        rows.add(row("Cliente", item.getCustomer() == null ? "" : text(item.getCustomer().getName())));
        rows.add(row("Valor", money(item.getAmount())));
        rows.add(row("Pago em", date(item.getPaymentDate())));
        rows.add(row("Forma de pagamento", item.getPaymentMethod() == null ? "" : text(item.getPaymentMethod().getName())));
        return new ReportData("Comprovante de Recebimento", columns, rows);
    }

    private ReportData payableVoucher(Long id) {
        Payable item = payableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta a pagar não encontrada. Id: " + id));

        validatePaid(item.getPaid());

        List<String> columns = Arrays.asList("Campo", "Valor");
        List<Map<String, ?>> rows = new ArrayList<>();
        rows.add(row("Conta", String.valueOf(item.getId())));
        rows.add(row("Descrição", text(item.getDescription())));
        rows.add(row("Fornecedor", item.getSupplier() == null ? "" : text(item.getSupplier().getName())));
        rows.add(row("Funcionário", item.getEmployee() == null ? "" : text(item.getEmployee().getName())));
        rows.add(row("Valor", money(item.getAmount())));
        rows.add(row("Pago em", date(item.getPaymentDate())));
        rows.add(row("Forma de pagamento", item.getPaymentMethod() == null ? "" : text(item.getPaymentMethod().getName())));
        return new ReportData("Comprovante de Pagamento", columns, rows);
    }

    private Specification<Receivable> receivableSpec(ReportFilterDTO filters) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (query != null) {
                root.fetch("customer", JoinType.LEFT);
                root.fetch("paymentMethod", JoinType.LEFT);
                query.distinct(true);
            }

            if (hasText(filters.getSearch())) {
                String search = "%" + filters.getSearch().trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("description")), search),
                        cb.like(cb.lower(root.get("reference")), search),
                        cb.like(cb.lower(root.join("customer", JoinType.LEFT).get("name")), search)
                ));
            }

            addCommonPredicates(cb, filters, predicates, root.get("paid"), root.get("canceled"), root.get("dueDate"),
                    root.get("paymentDate"), root.get("createdAt"), root.get("amount"), root.get("remainingBalance"));

            if (isValidId(filters.getCustomerId())) {
                predicates.add(cb.equal(root.get("customer").get("id"), filters.getCustomerId()));
            }

            if (isValidId(filters.getPaymentMethodId())) {
                predicates.add(cb.equal(root.get("paymentMethod").get("id"), filters.getPaymentMethodId()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Specification<Payable> payableSpec(ReportFilterDTO filters) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (query != null) {
                root.fetch("supplier", JoinType.LEFT);
                root.fetch("employee", JoinType.LEFT);
                root.fetch("paymentMethod", JoinType.LEFT);
                query.distinct(true);
            }

            if (hasText(filters.getSearch())) {
                String search = "%" + filters.getSearch().trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("description")), search),
                        cb.like(cb.lower(root.get("reference")), search),
                        cb.like(cb.lower(root.join("supplier", JoinType.LEFT).get("name")), search),
                        cb.like(cb.lower(root.join("employee", JoinType.LEFT).get("name")), search)
                ));
            }

            addCommonPredicates(cb, filters, predicates, root.get("paid"), root.get("canceled"), root.get("dueDate"),
                    root.get("paymentDate"), root.get("createdAt"), root.get("amount"), root.get("remainingBalance"));

            if (isValidId(filters.getSupplierId())) {
                predicates.add(cb.equal(root.get("supplier").get("id"), filters.getSupplierId()));
            }

            if (isValidId(filters.getEmployeeId())) {
                predicates.add(cb.equal(root.get("employee").get("id"), filters.getEmployeeId()));
            }

            if (isValidId(filters.getPaymentMethodId())) {
                predicates.add(cb.equal(root.get("paymentMethod").get("id"), filters.getPaymentMethodId()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void addCommonPredicates(
            CriteriaBuilder cb,
            ReportFilterDTO filters,
            List<Predicate> predicates,
            Path<Boolean> paidPath,
            Path<Boolean> canceledPath,
            Path<LocalDate> dueDatePath,
            Path<LocalDate> paymentDatePath,
            Path<java.time.Instant> createdAtPath,
            Path<BigDecimal> amountPath,
            Path<BigDecimal> remainingBalancePath
    ) {
        String status = filters.getStatus();

        if ("PAID".equals(status)) {
            predicates.add(cb.isTrue(paidPath));
            predicates.add(cb.isFalse(canceledPath));
        } else if ("PENDING".equals(status)) {
            predicates.add(cb.isFalse(paidPath));
            predicates.add(cb.isFalse(canceledPath));
            predicates.add(cb.or(cb.isNull(dueDatePath), cb.greaterThanOrEqualTo(dueDatePath, LocalDate.now(clock))));
        } else if ("OVERDUE".equals(status)) {
            predicates.add(cb.isFalse(paidPath));
            predicates.add(cb.isFalse(canceledPath));
            predicates.add(cb.lessThan(dueDatePath, LocalDate.now(clock)));
        } else if ("PARTIALLY_PAID".equals(status)) {
            predicates.add(cb.isFalse(paidPath));
            predicates.add(cb.isFalse(canceledPath));
            predicates.add(cb.greaterThan(remainingBalancePath, ZERO));
            predicates.add(cb.lessThan(remainingBalancePath, amountPath));
        } else if ("CANCELED".equals(status)) {
            predicates.add(cb.isTrue(canceledPath));
        }

        if (filters.getMinimumAmount() != null) {
            predicates.add(cb.greaterThanOrEqualTo(amountPath, filters.getMinimumAmount()));
        }

        if (filters.getMaximumAmount() != null) {
            predicates.add(cb.lessThanOrEqualTo(amountPath, filters.getMaximumAmount()));
        }

        if (filters.getStartDate() != null || filters.getEndDate() != null) {
            if ("CREATED_DATE".equals(filters.getPeriodType())) {
                if (filters.getStartDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(createdAtPath,
                            filters.getStartDate().atStartOfDay().toInstant(ZoneOffset.UTC)));
                }

                if (filters.getEndDate() != null) {
                    predicates.add(cb.lessThan(createdAtPath,
                            filters.getEndDate().plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC)));
                }
            } else {
                Path<LocalDate> datePath = "PAYMENT_DATE".equals(filters.getPeriodType()) ? paymentDatePath : dueDatePath;

                if (filters.getStartDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(datePath, filters.getStartDate()));
                }

                if (filters.getEndDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(datePath, filters.getEndDate()));
                }
            }
        }
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

    private void validatePaid(Boolean paid) {
        if (!Boolean.TRUE.equals(paid)) {
            throw new IllegalArgumentException("Comprovante disponível apenas para contas pagas.");
        }
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

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private boolean isValidId(Long id) {
        return id != null && id > 0;
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
