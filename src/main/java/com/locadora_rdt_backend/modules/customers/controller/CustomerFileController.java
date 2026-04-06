package com.locadora_rdt_backend.modules.customers.controller;

import com.locadora_rdt_backend.modules.customers.dto.file.CustomerFileDTO;
import com.locadora_rdt_backend.modules.customers.dto.file.CustomerFileResponseDTO;
import com.locadora_rdt_backend.modules.customers.service.CustomerFileService;
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

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{fileId}")
                .buildAndExpand(dto.getId())
                .toUri();

        return ResponseEntity.created(uri).body(dto);
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

        CustomerFileResponseDTO dto = service.download(customerId, fileId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(dto.getContentType()));
        headers.setContentDisposition(ContentDisposition.inline()
                .filename(dto.getFileName())
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(dto.getData());
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<byte[]> download(
            @PathVariable Long customerId,
            @PathVariable Long fileId) {

        CustomerFileResponseDTO dto = service.download(customerId, fileId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(dto.getContentType()));
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(dto.getFileName())
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(dto.getData());
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long customerId,
            @PathVariable Long fileId) {
        service.delete(customerId, fileId);
        return ResponseEntity.noContent().build();
    }
}
