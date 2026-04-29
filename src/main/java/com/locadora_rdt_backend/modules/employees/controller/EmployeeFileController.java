package com.locadora_rdt_backend.modules.employees.controller;

import com.locadora_rdt_backend.modules.employees.dto.file.EmployeeFileDTO;
import com.locadora_rdt_backend.modules.employees.dto.file.EmployeeFileViewDTO;
import com.locadora_rdt_backend.modules.employees.service.EmployeeFileService;
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
@RequestMapping("/employees/{employeeId}/files")
public class EmployeeFileController {

    private final EmployeeFileService service;

    public EmployeeFileController(EmployeeFileService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EmployeeFileDTO> upload(
            @PathVariable Long employeeId,
            @RequestParam("name") String name,
            @RequestParam("file") MultipartFile file) {

        EmployeeFileDTO dto = service.upload(employeeId, name, file);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{fileId}")
                .buildAndExpand(dto.getId())
                .toUri();

        return ResponseEntity.created(uri).body(dto);
    }

    @GetMapping
    public ResponseEntity<List<EmployeeFileDTO>> findAllByEmployee(@PathVariable Long employeeId) {
        List<EmployeeFileDTO> list = service.findAllByEmployee(employeeId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{fileId}/view")
    public ResponseEntity<byte[]> view(
            @PathVariable Long employeeId,
            @PathVariable Long fileId) {

        EmployeeFileViewDTO dto = service.download(employeeId, fileId);

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
            @PathVariable Long employeeId,
            @PathVariable Long fileId) {

        EmployeeFileViewDTO dto = service.download(employeeId, fileId);

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
            @PathVariable Long employeeId,
            @PathVariable Long fileId) {
        service.delete(employeeId, fileId);
        return ResponseEntity.noContent().build();
    }
}
