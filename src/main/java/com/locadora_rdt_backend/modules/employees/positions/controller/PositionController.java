package com.locadora_rdt_backend.modules.employees.positions.controller;

import com.locadora_rdt_backend.modules.employees.positions.dto.PositionDTO;
import com.locadora_rdt_backend.modules.employees.positions.dto.PositionDetailsDTO;
import com.locadora_rdt_backend.modules.employees.positions.dto.PositionInsertDTO;
import com.locadora_rdt_backend.modules.employees.positions.dto.PositionUpdateDTO;
import com.locadora_rdt_backend.modules.employees.positions.service.PositionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/positions")
public class PositionController {

    private final PositionService service;

    public PositionController(PositionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<PositionDTO>> findAllPaged(
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "10") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "name") String orderBy
    ) {
        PageRequest pageRequest = PageRequest.of(
                page,
                linesPerPage,
                Direction.valueOf(direction),
                orderBy
        );

        Page<PositionDTO> list = service.findAllPaged(name, pageRequest);

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PositionDetailsDTO> findById(@PathVariable Long id) {
        PositionDetailsDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }


    @PostMapping
    public ResponseEntity<PositionDTO> insert(@Valid @RequestBody PositionInsertDTO dto) {
        PositionDTO result = service.insert(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(result.getId())
                .toUri();

        return ResponseEntity.created(uri).body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PositionDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody PositionUpdateDTO dto
    ) {
        PositionDTO result = service.update(id, dto);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}