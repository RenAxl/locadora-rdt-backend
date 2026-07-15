package com.locadora_rdt_backend.modules.rental.controller;

import com.locadora_rdt_backend.modules.rental.dto.*;
import com.locadora_rdt_backend.modules.rental.service.RentalService;
import com.locadora_rdt_backend.shared.web.ControllerResponseBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
import com.locadora_rdt_backend.modules.customers.dto.CustomerDTO;

@RestController
@RequestMapping("/rentals")
public class RentalController {
    private final RentalService service;
    public RentalController(RentalService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<Page<RentalDTO>> findAll(
            @RequestParam(defaultValue = "") String number,
            @RequestParam(defaultValue = "") String customer,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(required = false) Long rentalTypeId,
            @RequestParam(required = false) Instant dateFrom,
            @RequestParam(required = false) Instant dateTo,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer linesPerPage,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(defaultValue = "rental_date") String orderBy) {
        PageRequest request = ControllerResponseBuilder.pageRequest(page, linesPerPage, direction, orderBy);
        return ResponseEntity.ok(service.findAllPaged(number, customer, status, rentalTypeId, dateFrom, dateTo, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentalDetailsDTO> findById(@PathVariable Long id) { return ResponseEntity.ok(service.findById(id)); }

    @GetMapping("/current-customer")
    public ResponseEntity<CustomerDTO> findCurrentCustomer() {
        return ResponseEntity.ok(service.findCurrentCustomer());
    }

    @PostMapping
    public ResponseEntity<RentalDTO> insert(@Valid @RequestBody RentalSaveDTO dto) {
        RentalDTO result = service.insert(dto);
        return ControllerResponseBuilder.created(result.getId(), result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RentalDTO> update(@PathVariable Long id, @Valid @RequestBody RentalSaveDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<RentalDTO> confirm(@PathVariable Long id) { return ResponseEntity.ok(service.confirm(id)); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
