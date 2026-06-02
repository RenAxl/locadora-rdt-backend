package com.locadora_rdt_backend.modules.suppliers.controller;

import com.locadora_rdt_backend.modules.suppliers.dto.SupplierFileDTO;
import com.locadora_rdt_backend.modules.suppliers.dto.SupplierFileViewDTO;
import com.locadora_rdt_backend.modules.suppliers.service.SupplierFileService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/suppliers/{supplierId}/files")
public class SupplierFileController {

    private final SupplierFileService service;

    public SupplierFileController(SupplierFileService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SupplierFileDTO> upload(
            @PathVariable Long supplierId,
            @RequestParam("name") String name,
            @RequestParam("file") MultipartFile file
    ) {
        SupplierFileDTO dto = service.upload(supplierId, name, file);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{fileId}")
                .buildAndExpand(dto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @GetMapping
    public ResponseEntity<List<SupplierFileDTO>> findAllBySupplier(
            @PathVariable Long supplierId
    ) {
        return ResponseEntity.ok(service.findAllBySupplier(supplierId));
    }

    @GetMapping("/{fileId}/view")
    public ResponseEntity<byte[]> view(
            @PathVariable Long supplierId,
            @PathVariable Long fileId
    ) {
        return buildFileResponse(service.download(supplierId, fileId), false);
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<byte[]> download(
            @PathVariable Long supplierId,
            @PathVariable Long fileId
    ) {
        return buildFileResponse(service.download(supplierId, fileId), true);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long supplierId,
            @PathVariable Long fileId
    ) {
        service.delete(supplierId, fileId);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<byte[]> buildFileResponse(
            SupplierFileViewDTO dto,
            boolean attachment
    ) {
        ContentDisposition disposition = attachment
                ? ContentDisposition.attachment().filename(dto.getFileName()).build()
                : ContentDisposition.inline().filename(dto.getFileName()).build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(dto.getContentType()));
        headers.setContentDisposition(disposition);

        return ResponseEntity.ok()
                .headers(headers)
                .body(dto.getData());
    }
}
