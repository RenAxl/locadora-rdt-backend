package com.locadora_rdt_backend.modules.organization.suppliers.controller;

import com.locadora_rdt_backend.modules.organization.suppliers.dto.SupplierFileDTO;
import com.locadora_rdt_backend.modules.organization.suppliers.service.SupplierFileService;
import com.locadora_rdt_backend.shared.web.BinaryResponseBuilder;
import com.locadora_rdt_backend.shared.web.ControllerResponseBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.locadora_rdt_backend.shared.constants.PermissionConstants.*;

@RestController
@RequestMapping("/suppliers/{supplierId}/files")
public class SupplierFileController {

    private final SupplierFileService service;

    public SupplierFileController(SupplierFileService service) {
        this.service = service;
    }

    @PreAuthorize(SUPPLIERS_WRITE)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SupplierFileDTO> upload(
            @PathVariable Long supplierId,
            @RequestParam("name") String name,
            @RequestParam("file") MultipartFile file
    ) {
        SupplierFileDTO dto = service.upload(supplierId, name, file);
        return ControllerResponseBuilder.created("/{fileId}", dto.getId(), dto);
    }

    @PreAuthorize(SUPPLIERS_READ)
    @GetMapping
    public ResponseEntity<List<SupplierFileDTO>> findAllBySupplier(
            @PathVariable Long supplierId
    ) {
        return ResponseEntity.ok(service.findAllBySupplier(supplierId));
    }

    @PreAuthorize(SUPPLIERS_READ)
    @GetMapping("/{fileId}/view")
    public ResponseEntity<byte[]> view(
            @PathVariable Long supplierId,
            @PathVariable Long fileId
    ) {
        return BinaryResponseBuilder.inlineFile(service.download(supplierId, fileId));
    }

    @PreAuthorize(SUPPLIERS_READ)
    @GetMapping("/{fileId}/download")
    public ResponseEntity<byte[]> download(
            @PathVariable Long supplierId,
            @PathVariable Long fileId
    ) {
        return BinaryResponseBuilder.attachmentFile(service.download(supplierId, fileId));
    }

    @PreAuthorize(SUPPLIERS_DELETE)
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long supplierId,
            @PathVariable Long fileId
    ) {
        service.delete(supplierId, fileId);
        return ResponseEntity.noContent().build();
    }
}
