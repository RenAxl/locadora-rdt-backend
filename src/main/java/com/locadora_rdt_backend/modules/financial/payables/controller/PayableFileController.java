package com.locadora_rdt_backend.modules.financial.payables.controller;

import com.locadora_rdt_backend.modules.financial.payables.dto.PayableFileDTO;
import com.locadora_rdt_backend.modules.financial.payables.service.PayableFileService;
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
@RequestMapping("/payables/{payableId}/files")
public class PayableFileController {

    private final PayableFileService service;

    public PayableFileController(PayableFileService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PayableFileDTO> upload(
            @PathVariable Long payableId,
            @RequestParam("name") String name,
            @RequestParam("file") MultipartFile file
    ) {
        PayableFileDTO dto = service.upload(payableId, name, file);
        return ControllerResponseBuilder.created("/{fileId}", dto.getId(), dto);
    }

    @GetMapping
    public ResponseEntity<List<PayableFileDTO>> findAllByPayable(@PathVariable Long payableId) {
        return ResponseEntity.ok(service.findAllByPayable(payableId));
    }

    @GetMapping("/{fileId}/view")
    public ResponseEntity<byte[]> view(
            @PathVariable Long payableId,
            @PathVariable Long fileId
    ) {
        return BinaryResponseBuilder.inlineFile(service.download(payableId, fileId));
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<byte[]> download(
            @PathVariable Long payableId,
            @PathVariable Long fileId
    ) {
        return BinaryResponseBuilder.attachmentFile(service.download(payableId, fileId));
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long payableId,
            @PathVariable Long fileId
    ) {
        service.delete(payableId, fileId);
        return ResponseEntity.noContent().build();
    }
}
