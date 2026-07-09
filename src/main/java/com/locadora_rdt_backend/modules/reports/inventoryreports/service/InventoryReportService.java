package com.locadora_rdt_backend.modules.reports.inventoryreports.service;

import com.locadora_rdt_backend.modules.reports.inventoryreports.dto.InventoryReportFilterDTO;
import com.locadora_rdt_backend.shared.reports.ReportFileDTO;

public interface InventoryReportService {

    ReportFileDTO generate(String reportType, String format, InventoryReportFilterDTO filters);
}
