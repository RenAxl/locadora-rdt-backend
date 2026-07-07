package com.locadora_rdt_backend.modules.reports.controller;

import com.locadora_rdt_backend.modules.reports.dto.ReportFileDTO;
import com.locadora_rdt_backend.modules.reports.dto.ReportFilterDTO;
import com.locadora_rdt_backend.modules.reports.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
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

    @GetMapping("/vouchers/{accountType}/{accountId}/{format}")
    public ResponseEntity<byte[]> voucher(
            @PathVariable String accountType,
            @PathVariable Long accountId,
            @PathVariable String format
    ) {
        ReportFileDTO file = service.voucher(accountType, accountId, format);
        return buildFileResponse(file);
    }

    private ResponseEntity<byte[]> buildFileResponse(ReportFileDTO file) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getFileName())
                .body(file.getContent());
    }
}
