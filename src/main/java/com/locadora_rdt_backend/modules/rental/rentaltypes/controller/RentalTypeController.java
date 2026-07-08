package com.locadora_rdt_backend.modules.rental.rentaltypes.controller;

import com.locadora_rdt_backend.modules.rental.rentaltypes.dto.RentalTypeDTO;
import com.locadora_rdt_backend.modules.rental.rentaltypes.dto.RentalTypeDetailsDTO;
import com.locadora_rdt_backend.modules.rental.rentaltypes.dto.RentalTypeInsertDTO;
import com.locadora_rdt_backend.modules.rental.rentaltypes.dto.RentalTypeUpdateDTO;
import com.locadora_rdt_backend.modules.rental.rentaltypes.service.RentalTypeService;
import com.locadora_rdt_backend.shared.web.ControllerResponseBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/rental/rentaltypes")
public class RentalTypeController {

    private final RentalTypeService service;

    public RentalTypeController(RentalTypeService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<RentalTypeDTO>> findAllPaged(
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "10") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "name") String orderBy
    ) {
        PageRequest pageRequest = ControllerResponseBuilder.pageRequest(page, linesPerPage, direction, orderBy);

        Page<RentalTypeDTO> list = service.findAllPaged(name.trim(), pageRequest);

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentalTypeDetailsDTO> findById(@PathVariable Long id) {
        RentalTypeDetailsDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<RentalTypeDTO> insert(@Valid @RequestBody RentalTypeInsertDTO dto) {
        RentalTypeDTO result = service.insert(dto);

        return ControllerResponseBuilder.created(result.getId(), result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RentalTypeDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody RentalTypeUpdateDTO dto
    ) {
        RentalTypeDTO result = service.update(id, dto);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAll(@RequestBody List<Long> ids) {
        service.deleteAll(ids);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<Void> changeActive(
            @PathVariable Long id,
            @RequestBody boolean active
    ) {
        service.changeActiveStatus(id, active);
        return ResponseEntity.noContent().build();
    }
}
