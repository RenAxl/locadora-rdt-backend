package com.locadora_rdt_backend.modules.reports.financialreports.service;

import com.locadora_rdt_backend.modules.reports.financialreports.dto.ReportComparisonDTO;
import com.locadora_rdt_backend.modules.reports.financialreports.dto.ReportFileDTO;
import com.locadora_rdt_backend.modules.reports.financialreports.dto.ReportFilterDTO;

public interface ReportService {

    ReportFileDTO generate(String reportType, String format, ReportFilterDTO filters);

    ReportComparisonDTO comparison(ReportFilterDTO filters);
}
