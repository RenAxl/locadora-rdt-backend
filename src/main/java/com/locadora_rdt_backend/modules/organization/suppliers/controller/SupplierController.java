package com.locadora_rdt_backend.modules.organization.suppliers.controller;

import com.locadora_rdt_backend.modules.organization.suppliers.dto.SupplierDTO;
import com.locadora_rdt_backend.modules.organization.suppliers.dto.SupplierDetailsDTO;
import com.locadora_rdt_backend.modules.organization.suppliers.dto.SupplierInsertDTO;
import com.locadora_rdt_backend.modules.organization.suppliers.dto.SupplierUpdateDTO;
import com.locadora_rdt_backend.modules.organization.suppliers.model.Supplier;
import com.locadora_rdt_backend.modules.organization.suppliers.service.SupplierService;
import com.locadora_rdt_backend.shared.web.BinaryResponseBuilder;
import com.locadora_rdt_backend.shared.web.ControllerResponseBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {

    private final SupplierService service;

    public SupplierController(SupplierService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<SupplierDTO>> findAllPaged(
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "10") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "name") String orderBy
    ) {
        PageRequest pageRequest = ControllerResponseBuilder.pageRequest(page, linesPerPage, direction, orderBy);
        return ResponseEntity.ok(service.findAllPaged(name, pageRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierDetailsDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<SupplierDTO> insert(@Valid @RequestBody SupplierInsertDTO dto) {
        SupplierDTO result = service.insert(dto);
        return ControllerResponseBuilder.created(result.getId(), result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody SupplierUpdateDTO dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @PutMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        service.updateImage(id, file);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        Supplier entity = service.findEntityById(id);

        return BinaryResponseBuilder.media(entity.getImage(), entity.getImageContentType());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
