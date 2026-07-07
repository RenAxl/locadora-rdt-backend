package com.locadora_rdt_backend.modules.reports.service;

import com.locadora_rdt_backend.modules.reports.dto.ReportFileDTO;
import com.locadora_rdt_backend.modules.reports.dto.ReportFilterDTO;

public interface ReportService {

    ReportFileDTO generate(String reportType, String format, ReportFilterDTO filters);

    ReportFileDTO voucher(String accountType, Long accountId, String format);
}
