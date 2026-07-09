package com.locadora_rdt_backend.modules.reports.inventoryreports.service;

import com.locadora_rdt_backend.modules.reports.financialreports.dto.ReportFileDTO;
import com.locadora_rdt_backend.modules.reports.inventoryreports.dto.InventoryReportFilterDTO;

public interface InventoryReportService {

    ReportFileDTO generate(String reportType, String format, InventoryReportFilterDTO filters);
}
