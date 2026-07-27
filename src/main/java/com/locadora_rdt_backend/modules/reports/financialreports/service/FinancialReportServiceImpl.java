package com.locadora_rdt_backend.modules.reports.financialreports.service;

import com.locadora_rdt_backend.modules.financial.payables.model.Payable;
import com.locadora_rdt_backend.modules.financial.receivables.model.Receivable;
import com.locadora_rdt_backend.modules.reports.financialreports.dto.FinancialReportComparisonDTO;
import com.locadora_rdt_backend.modules.reports.financialreports.dto.FinancialReportFilterDTO;
import com.locadora_rdt_backend.modules.reports.financialreports.model.FinancialReportType;
import com.locadora_rdt_backend.shared.reports.JasperReportGenerator;
import com.locadora_rdt_backend.shared.reports.ReportData;
import com.locadora_rdt_backend.shared.reports.ReportFileDTO;
import com.locadora_rdt_backend.shared.reports.ReportFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Service
public class FinancialReportServiceImpl implements FinancialReportService {

    private final FinancialReportQueryService queryService;
    private final FinancialReportCalculationService calculationService;
    private final FinancialReportTableService tableService;
    private final JasperReportGenerator jasperReportGenerator;
    private final Clock clock;

    public FinancialReportServiceImpl(
            FinancialReportQueryService queryService,
            FinancialReportCalculationService calculationService,
            FinancialReportTableService tableService,
            JasperReportGenerator jasperReportGenerator,
            Clock clock
    ) {
        this.queryService = queryService;
        this.calculationService = calculationService;
        this.tableService = tableService;
        this.jasperReportGenerator = jasperReportGenerator;
        this.clock = clock;
    }

    @Override
    @Transactional(readOnly = true)
    public ReportFileDTO generate(String reportTypeValue, String formatValue, FinancialReportFilterDTO filters) {
        FinancialReportType financialReportType = FinancialReportType.from(reportTypeValue);
        ReportFormat format = ReportFormat.from(formatValue);
        FinancialReportFilterDTO normalizedFilters = queryService.normalize(filters);

        ReportData data = buildReportData(financialReportType, normalizedFilters);

        byte[] content = jasperReportGenerator.generate(
                data.getTitle(),
                data.getColumns(),
                data.getRows(),
                format
        );

        String fileName = financialReportType.name().toLowerCase() + "." + format.getExtension();
        return new ReportFileDTO(fileName, format.getContentType(), content);
    }

    @Override
    @Transactional(readOnly = true)
    public FinancialReportComparisonDTO comparison(FinancialReportFilterDTO filters) {
        FinancialReportFilterDTO normalizedFilters = queryService.normalize(filters);
        int year = normalizedFilters.getYear() == null ? LocalDate.now(clock).getYear() : normalizedFilters.getYear();

        FinancialReportFilterDTO comparisonFilters = queryService.copy(normalizedFilters);
        comparisonFilters.setStartDate(LocalDate.of(year, 1, 1));
        comparisonFilters.setEndDate(LocalDate.of(year, 12, 31));

        List<Receivable> receivables = queryService.findReceivables(comparisonFilters);
        List<Payable> payables = queryService.findPayables(comparisonFilters);

        BigDecimal receivableTotal = calculationService.sumReceivables(receivables);
        BigDecimal payableTotal = calculationService.sumPayables(payables);

        return new FinancialReportComparisonDTO(
                receivableTotal,
                payableTotal,
                receivableTotal.subtract(payableTotal),
                receivables.size(),
                payables.size(),
                year,
                calculationService.monthlyComparison(receivables, payables, comparisonFilters.getPeriodType())
        );
    }

    private ReportData buildReportData(FinancialReportType type, FinancialReportFilterDTO filters) {
        if (type == FinancialReportType.RECEIVABLES) {
            List<Receivable> items = queryService.findReceivables(filters);
            return tableService.receivablesReport(items);
        }

        if (type == FinancialReportType.PAYABLES) {
            List<Payable> items = queryService.findPayables(filters);
            return tableService.payablesReport(items);
        }

        if (type == FinancialReportType.FINANCIAL) {
            List<Receivable> receivables = queryService.findReceivables(filters);
            List<Payable> payables = queryService.findPayables(filters);
            return tableService.financialReport(receivables, payables);
        }

        if (type == FinancialReportType.SUMMARY_CUSTOMER) {
            List<Receivable> items = queryService.findReceivables(filters);
            return tableService.summaryReport(
                    "Relatório Sintético por Cliente",
                    "Cliente",
                    calculationService.groupReceivablesByCustomer(items)
            );
        }

        if (type == FinancialReportType.SUMMARY_SUPPLIER) {
            List<Payable> items = queryService.findPayables(filters);
            return tableService.summaryReport(
                    "Relatório Sintético por Fornecedor",
                    "Fornecedor",
                    calculationService.groupPayablesBySupplier(items)
            );
        }

        if (type == FinancialReportType.SUMMARY_EMPLOYEE) {
            List<Payable> items = queryService.findPayables(filters);
            return tableService.summaryReport(
                    "Relatório Sintético por Funcionário",
                    "Funcionário",
                    calculationService.groupPayablesByEmployee(items)
            );
        }

        return annualBalanceReport(filters);
    }

    private ReportData annualBalanceReport(FinancialReportFilterDTO filters) {
        int year = filters.getYear() == null ? LocalDate.now(clock).getYear() : filters.getYear();

        FinancialReportFilterDTO annualFilters = queryService.copy(filters);
        annualFilters.setStatus("PAID");
        annualFilters.setPeriodType("PAYMENT_DATE");
        annualFilters.setStartDate(LocalDate.of(year, 1, 1));
        annualFilters.setEndDate(LocalDate.of(year, 12, 31));

        List<Receivable> receivables = queryService.findReceivables(annualFilters);
        List<Payable> payables = queryService.findPayables(annualFilters);

        return tableService.annualBalanceReport(year, receivables, payables);
    }
}
