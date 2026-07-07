package com.locadora_rdt_backend.modules.reports.service;

import com.locadora_rdt_backend.modules.reports.dto.ReportComparisonDTO;
import com.locadora_rdt_backend.modules.reports.dto.ReportFileDTO;
import com.locadora_rdt_backend.modules.reports.dto.ReportFilterDTO;

public interface ReportService {

    ReportFileDTO generate(String reportType, String format, ReportFilterDTO filters);

    ReportComparisonDTO comparison(ReportFilterDTO filters);
}
