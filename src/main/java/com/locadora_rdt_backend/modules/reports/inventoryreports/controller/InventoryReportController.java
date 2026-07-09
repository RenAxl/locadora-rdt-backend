package com.locadora_rdt_backend.modules.reports.inventoryreports.controller;

import com.locadora_rdt_backend.modules.reports.financialreports.dto.ReportFileDTO;
import com.locadora_rdt_backend.modules.reports.inventoryreports.dto.InventoryReportFilterDTO;
import com.locadora_rdt_backend.modules.reports.inventoryreports.service.InventoryReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports/inventory-reports")
public class InventoryReportController {

    private final InventoryReportService service;

    public InventoryReportController(InventoryReportService service) {
        this.service = service;
    }

    @GetMapping("/{reportType}/{format}")
    public ResponseEntity<byte[]> generate(
            @PathVariable String reportType,
            @PathVariable String format,
            @ModelAttribute InventoryReportFilterDTO filters
    ) {
        ReportFileDTO file = service.generate(reportType, format, filters);
        return buildFileResponse(file);
    }

    private ResponseEntity<byte[]> buildFileResponse(ReportFileDTO file) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getFileName())
                .body(file.getContent());
    }
}
