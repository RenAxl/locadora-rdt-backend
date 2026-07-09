package com.locadora_rdt_backend.modules.reports.financialreports.service;

import com.locadora_rdt_backend.modules.reports.financialreports.dto.ReportComparisonDTO;
import com.locadora_rdt_backend.modules.reports.financialreports.dto.ReportFilterDTO;
import com.locadora_rdt_backend.shared.reports.ReportFileDTO;

public interface ReportService {

    ReportFileDTO generate(String reportType, String format, ReportFilterDTO filters);

    ReportComparisonDTO comparison(ReportFilterDTO filters);
}
