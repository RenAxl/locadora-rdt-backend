package com.locadora_rdt_backend.modules.receivables.controller;

import com.locadora_rdt_backend.modules.receivables.dto.ReceivableFileDTO;
import com.locadora_rdt_backend.modules.receivables.service.ReceivableFileService;
import com.locadora_rdt_backend.shared.web.BinaryResponseBuilder;
import com.locadora_rdt_backend.shared.web.ControllerResponseBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/receivables/{receivableId}/files")
public class ReceivableFileController {

    private final ReceivableFileService service;

    public ReceivableFileController(ReceivableFileService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReceivableFileDTO> upload(
            @PathVariable Long receivableId,
            @RequestParam("name") String name,
            @RequestParam("file") MultipartFile file
    ) {
        ReceivableFileDTO dto = service.upload(receivableId, name, file);
        return ControllerResponseBuilder.created("/{fileId}", dto.getId(), dto);
    }

    @GetMapping
    public ResponseEntity<List<ReceivableFileDTO>> findAllByReceivable(@PathVariable Long receivableId) {
        return ResponseEntity.ok(service.findAllByReceivable(receivableId));
    }

    @GetMapping("/{fileId}/view")
    public ResponseEntity<byte[]> view(
            @PathVariable Long receivableId,
            @PathVariable Long fileId
    ) {
        return BinaryResponseBuilder.inlineFile(service.download(receivableId, fileId));
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<byte[]> download(
            @PathVariable Long receivableId,
            @PathVariable Long fileId
    ) {
        return BinaryResponseBuilder.attachmentFile(service.download(receivableId, fileId));
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long receivableId,
            @PathVariable Long fileId
    ) {
        service.delete(receivableId, fileId);
        return ResponseEntity.noContent().build();
    }
}
