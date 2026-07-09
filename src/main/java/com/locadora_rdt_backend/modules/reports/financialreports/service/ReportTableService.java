package com.locadora_rdt_backend.modules.reports.financialreports.service;

import com.locadora_rdt_backend.modules.financial.payables.model.Payable;
import com.locadora_rdt_backend.modules.financial.receivables.model.Receivable;
import com.locadora_rdt_backend.shared.reports.ReportData;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.locadora_rdt_backend.shared.reports.ReportTableSupport.date;
import static com.locadora_rdt_backend.shared.reports.ReportTableSupport.money;
import static com.locadora_rdt_backend.shared.reports.ReportTableSupport.row;
import static com.locadora_rdt_backend.shared.reports.ReportTableSupport.text;
import static com.locadora_rdt_backend.shared.reports.ReportTableSupport.valueOrZero;

@Service
public class ReportTableService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private final ReportCalculationService calculationService;
    private final Clock clock;

    public ReportTableService(ReportCalculationService calculationService, Clock clock) {
        this.calculationService = calculationService;
        this.clock = clock;
    }

    public ReportData receivablesReport(List<Receivable> items) {
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

        addTotalRow(rows, "Total", calculationService.sumReceivables(items), columns.size());
        return new ReportData("Relatório de Contas a Receber", columns, rows);
    }

    public ReportData payablesReport(List<Payable> items) {
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

        addTotalRow(rows, "Total", calculationService.sumPayables(items), columns.size());
        return new ReportData("Relatório de Contas a Pagar", columns, rows);
    }

    public ReportData financialReport(List<Receivable> receivables, List<Payable> payables) {
        BigDecimal revenue = calculationService.sumReceivables(receivables);
        BigDecimal expense = calculationService.sumPayables(payables);
        BigDecimal received = calculationService.sumPaidReceivables(receivables);
        BigDecimal paid = calculationService.sumPaidPayables(payables);
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

    public ReportData summaryReport(
            String title,
            String firstColumn,
            Map<String, ReportCalculationService.SummaryValues> grouped
    ) {
        List<String> columns = Arrays.asList(firstColumn, "Quantidade", "Total", "Total pago/recebido", "Em aberto");
        List<Map<String, ?>> rows = new ArrayList<>();

        for (Map.Entry<String, ReportCalculationService.SummaryValues> entry : grouped.entrySet()) {
            ReportCalculationService.SummaryValues values = entry.getValue();
            rows.add(row(
                    entry.getKey(),
                    String.valueOf(values.getQuantity()),
                    money(values.getTotal()),
                    money(values.getPaid()),
                    money(values.getTotal().subtract(values.getPaid()))
            ));
        }

        return new ReportData(title, columns, rows);
    }

    public ReportData annualBalanceReport(int year, List<Receivable> receivables, List<Payable> payables) {
        List<String> columns = Arrays.asList("Mês", "Total recebido", "Total pago", "Saldo");
        List<Map<String, ?>> rows = new ArrayList<>();

        BigDecimal yearReceived = ZERO;
        BigDecimal yearPaid = ZERO;

        for (Month month : Month.values()) {
            BigDecimal received = calculationService.sumPaidReceivablesByMonth(receivables, month.getValue());
            BigDecimal paid = calculationService.sumPaidPayablesByMonth(payables, month.getValue());
            yearReceived = yearReceived.add(received);
            yearPaid = yearPaid.add(paid);
            rows.add(row(monthName(month), money(received), money(paid), money(received.subtract(paid))));
        }

        rows.add(row("Total do ano", money(yearReceived), money(yearPaid), money(yearReceived.subtract(yearPaid))));
        return new ReportData("Balanço Anual " + year, columns, rows);
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

    private String monthName(Month month) {
        String[] names = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        return names[month.getValue() - 1];
    }
}
