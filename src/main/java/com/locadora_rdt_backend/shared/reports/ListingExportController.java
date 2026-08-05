package com.locadora_rdt_backend.shared.reports;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/listing-exports")
public class ListingExportController {

    private static final String XLSX_MEDIA_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final ListingExportService service;

    public ListingExportController(ListingExportService service) {
        this.service = service;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/excel", produces = XLSX_MEDIA_TYPE)
    public ResponseEntity<byte[]> exportExcel(@RequestBody ListingExportRequest request) {
        byte[] file = service.export(request);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(XLSX_MEDIA_TYPE))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=listagem.xlsx")
                .body(file);
    }
}
