package com.locadora_rdt_backend.modules.employees.departments.controller;

import com.locadora_rdt_backend.modules.employees.departments.dto.DepartmentDTO;
import com.locadora_rdt_backend.modules.employees.departments.dto.DepartmentDetailsDTO;
import com.locadora_rdt_backend.modules.employees.departments.dto.DepartmentInsertDTO;
import com.locadora_rdt_backend.modules.employees.departments.dto.DepartmentUpdateDTO;
import com.locadora_rdt_backend.modules.employees.departments.service.DepartmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping(value = "/departments")
public class DepartmentController {

    private final DepartmentService service;

    public DepartmentController(DepartmentService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<DepartmentDTO>> findAllPaged(
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "3") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "name") String orderBy
    ) {
        PageRequest pageRequest = PageRequest.of(
                page,
                linesPerPage,
                Direction.valueOf(direction),
                orderBy
        );

        Page<DepartmentDTO> list = service.findAllPaged(name.trim(), pageRequest);

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDetailsDTO> findById(@PathVariable Long id) {
        DepartmentDetailsDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<DepartmentDTO> insert(@Valid @RequestBody DepartmentInsertDTO dto) {
        DepartmentDTO result = service.insert(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(result.getId())
                .toUri();

        return ResponseEntity.created(uri).body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentUpdateDTO dto
    ) {
        DepartmentDTO result = service.update(id, dto);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}