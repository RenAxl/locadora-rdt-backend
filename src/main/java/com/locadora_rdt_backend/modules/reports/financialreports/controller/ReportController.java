package com.locadora_rdt_backend.modules.reports.financialreports.controller;

import com.locadora_rdt_backend.modules.reports.financialreports.dto.ReportComparisonDTO;
import com.locadora_rdt_backend.modules.reports.financialreports.dto.ReportFilterDTO;
import com.locadora_rdt_backend.modules.reports.financialreports.service.ReportService;
import com.locadora_rdt_backend.shared.reports.ReportFileDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports/financial-reports")
public class ReportController {

    private final ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    @GetMapping("/{reportType}/{format}")
    public ResponseEntity<byte[]> generate(
            @PathVariable String reportType,
            @PathVariable String format,
            @ModelAttribute ReportFilterDTO filters
    ) {
        ReportFileDTO file = service.generate(reportType, format, filters);
        return buildFileResponse(file);
    }

    @GetMapping("/comparison")
    public ResponseEntity<ReportComparisonDTO> comparison(
            @ModelAttribute ReportFilterDTO filters
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
