package com.locadora_rdt_backend.modules.reports.inventoryreports.service;

import com.locadora_rdt_backend.modules.inventory.stockbalances.model.StockBalance;
import com.locadora_rdt_backend.modules.inventory.stockmovements.model.StockMovement;
import com.locadora_rdt_backend.modules.reports.inventoryreports.dto.InventoryReportFilterDTO;
import com.locadora_rdt_backend.modules.reports.inventoryreports.model.InventoryReportType;
import com.locadora_rdt_backend.shared.reports.JasperReportGenerator;
import com.locadora_rdt_backend.shared.reports.ReportData;
import com.locadora_rdt_backend.shared.reports.ReportFileDTO;
import com.locadora_rdt_backend.shared.reports.ReportFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryReportServiceImpl implements InventoryReportService {

    private final InventoryReportQueryService queryService;
    private final InventoryReportTableService tableService;
    private final JasperReportGenerator jasperReportGenerator;

    public InventoryReportServiceImpl(
            InventoryReportQueryService queryService,
            InventoryReportTableService tableService,
            JasperReportGenerator jasperReportGenerator
    ) {
        this.queryService = queryService;
        this.tableService = tableService;
        this.jasperReportGenerator = jasperReportGenerator;
    }

    @Override
    @Transactional(readOnly = true)
    public ReportFileDTO generate(String reportTypeValue, String formatValue, InventoryReportFilterDTO filters) {
        InventoryReportType reportType = InventoryReportType.from(reportTypeValue);
        ReportFormat format = ReportFormat.from(formatValue);
        InventoryReportFilterDTO normalizedFilters = queryService.normalize(filters);

        ReportData data = buildReportData(reportType, normalizedFilters);

        byte[] content = jasperReportGenerator.generate(
                data.getTitle(),
                data.getColumns(),
                data.getRows(),
                format
        );

        String fileName = reportType.name().toLowerCase() + "." + format.getExtension();
        return new ReportFileDTO(fileName, format.getContentType(), content);
    }

    private ReportData buildReportData(InventoryReportType type, InventoryReportFilterDTO filters) {
        if (type == InventoryReportType.CURRENT_STOCK) {
            List<StockBalance> items = queryService.findCurrentStock(filters);
            return tableService.currentStockReport(items);
        }

        if (type == InventoryReportType.LOW_STOCK) {
            List<StockBalance> items = queryService.findLowStock(filters);
            return tableService.lowStockReport(items);
        }

        List<StockMovement> items = queryService.findMovementHistory(filters);
        return tableService.movementHistoryReport(items);
    }
}
