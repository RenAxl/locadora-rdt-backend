package com.locadora_rdt_backend.modules.reports.financialreports.service;

import com.locadora_rdt_backend.modules.reports.financialreports.dto.FinancialReportComparisonDTO;
import com.locadora_rdt_backend.modules.reports.financialreports.dto.FinancialReportFilterDTO;
import com.locadora_rdt_backend.shared.reports.ReportFileDTO;

public interface FinancialReportService {

    ReportFileDTO generate(String reportType, String format, FinancialReportFilterDTO filters);

    FinancialReportComparisonDTO comparison(FinancialReportFilterDTO filters);
}
