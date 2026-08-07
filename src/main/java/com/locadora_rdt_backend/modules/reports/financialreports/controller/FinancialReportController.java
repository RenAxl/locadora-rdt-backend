package com.locadora_rdt_backend.modules.reports.financialreports.controller;

import com.locadora_rdt_backend.modules.reports.financialreports.dto.FinancialReportComparisonDTO;
import com.locadora_rdt_backend.modules.reports.financialreports.dto.FinancialReportFilterDTO;
import com.locadora_rdt_backend.modules.reports.financialreports.service.FinancialReportService;
import com.locadora_rdt_backend.shared.reports.ReportFileDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.locadora_rdt_backend.shared.constants.PermissionConstants.FINANCIALREPORTS_READ;

@RestController
@RequestMapping("/reports/financial-reports")
public class FinancialReportController {

    private final FinancialReportService service;

    public FinancialReportController(FinancialReportService service) {
        this.service = service;
    }

    @PreAuthorize(FINANCIALREPORTS_READ)
    @GetMapping("/{reportType}/{format}")
    public ResponseEntity<byte[]> generate(
            @PathVariable String reportType,
            @PathVariable String format,
            @ModelAttribute FinancialReportFilterDTO filters
    ) {
        ReportFileDTO file = service.generate(reportType, format, filters);
        return buildFileResponse(file);
    }

    @PreAuthorize(FINANCIALREPORTS_READ)
    @GetMapping("/comparison")
    public ResponseEntity<FinancialReportComparisonDTO> comparison(
            @ModelAttribute FinancialReportFilterDTO filters
    ) {
        return ResponseEntity.ok(service.comparison(filters));
    }

    private ResponseEntity<byte[]> buildFileResponse(ReportFileDTO file) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getFileName())
                .body(file.getContent());
    }
}
