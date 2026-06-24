package com.locadora_rdt_backend.modules.customers.controller;

import com.locadora_rdt_backend.modules.customers.dto.CustomerFileDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerFileViewDTO;
import com.locadora_rdt_backend.modules.customers.service.CustomerFileService;
import com.locadora_rdt_backend.shared.web.BinaryResponseBuilder;
import com.locadora_rdt_backend.shared.web.ControllerResponseBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/customers/{customerId}/files")
public class CustomerFileController {

    private final CustomerFileService service;

    public CustomerFileController(CustomerFileService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomerFileDTO> upload(
            @PathVariable Long customerId,
            @RequestParam("name") String name,
            @RequestParam("file") MultipartFile file) {

        CustomerFileDTO dto = service.upload(customerId, name, file);

        return ControllerResponseBuilder.created("/{fileId}", dto.getId(), dto);
    }

    @GetMapping
    public ResponseEntity<List<CustomerFileDTO>> findAllByCustomer(@PathVariable Long customerId) {
        List<CustomerFileDTO> list = service.findAllByCustomer(customerId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{fileId}/view")
    public ResponseEntity<byte[]> view(
            @PathVariable Long customerId,
            @PathVariable Long fileId) {

        CustomerFileViewDTO dto = service.download(customerId, fileId);

        return BinaryResponseBuilder.inlineFile(dto);
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<byte[]> download(
            @PathVariable Long customerId,
            @PathVariable Long fileId) {

        CustomerFileViewDTO dto = service.download(customerId, fileId);

        return BinaryResponseBuilder.attachmentFile(dto);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long customerId,
            @PathVariable Long fileId) {
        service.delete(customerId, fileId);
        return ResponseEntity.noContent().build();
    }
}
