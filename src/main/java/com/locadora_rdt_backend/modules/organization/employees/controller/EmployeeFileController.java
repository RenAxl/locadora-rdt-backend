package com.locadora_rdt_backend.modules.organization.employees.controller;

import com.locadora_rdt_backend.modules.organization.employees.dto.EmployeeFileDTO;
import com.locadora_rdt_backend.modules.organization.employees.dto.EmployeeFileViewDTO;
import com.locadora_rdt_backend.modules.organization.employees.service.EmployeeFileService;
import com.locadora_rdt_backend.shared.web.BinaryResponseBuilder;
import com.locadora_rdt_backend.shared.web.ControllerResponseBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

        return ControllerResponseBuilder.created("/{fileId}", dto.getId(), dto);
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

        return BinaryResponseBuilder.inlineFile(dto);
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<byte[]> download(
            @PathVariable Long employeeId,
            @PathVariable Long fileId) {

        EmployeeFileViewDTO dto = service.download(employeeId, fileId);

        return BinaryResponseBuilder.attachmentFile(dto);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long employeeId,
            @PathVariable Long fileId) {
        service.delete(employeeId, fileId);
        return ResponseEntity.noContent().build();
    }
}
