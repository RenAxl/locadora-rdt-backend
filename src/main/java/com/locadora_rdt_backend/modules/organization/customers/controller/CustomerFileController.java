package com.locadora_rdt_backend.modules.organization.customers.controller;

import com.locadora_rdt_backend.modules.organization.customers.dto.CustomerFileDTO;
import com.locadora_rdt_backend.modules.organization.customers.dto.CustomerFileViewDTO;
import com.locadora_rdt_backend.modules.organization.customers.service.CustomerFileService;
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
@RequestMapping("/customers/{customerId}/files")
public class CustomerFileController {

    private final CustomerFileService service;

    public CustomerFileController(CustomerFileService service) {
        this.service = service;
    }

    @PreAuthorize(CUSTOMERS_WRITE)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomerFileDTO> upload(
            @PathVariable Long customerId,
            @RequestParam("name") String name,
            @RequestParam("file") MultipartFile file) {

        CustomerFileDTO dto = service.upload(customerId, name, file);

        return ControllerResponseBuilder.created("/{fileId}", dto.getId(), dto);
    }

    @PreAuthorize(CUSTOMERS_READ)
    @GetMapping
    public ResponseEntity<List<CustomerFileDTO>> findAllByCustomer(@PathVariable Long customerId) {
        List<CustomerFileDTO> list = service.findAllByCustomer(customerId);
        return ResponseEntity.ok(list);
    }

    @PreAuthorize(CUSTOMERS_READ)
    @GetMapping("/{fileId}/view")
    public ResponseEntity<byte[]> view(
            @PathVariable Long customerId,
            @PathVariable Long fileId) {

        CustomerFileViewDTO dto = service.download(customerId, fileId);

        return BinaryResponseBuilder.inlineFile(dto);
    }

    @PreAuthorize(CUSTOMERS_READ)
    @GetMapping("/{fileId}/download")
    public ResponseEntity<byte[]> download(
            @PathVariable Long customerId,
            @PathVariable Long fileId) {

        CustomerFileViewDTO dto = service.download(customerId, fileId);

        return BinaryResponseBuilder.attachmentFile(dto);
    }

    @PreAuthorize(CUSTOMERS_DELETE)
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long customerId,
            @PathVariable Long fileId) {
        service.delete(customerId, fileId);
        return ResponseEntity.noContent().build();
    }
}
