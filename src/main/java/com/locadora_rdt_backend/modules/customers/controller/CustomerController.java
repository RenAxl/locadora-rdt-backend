package com.locadora_rdt_backend.modules.customers.controller;

import com.locadora_rdt_backend.modules.customers.dto.CustomerDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerInsertDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerUpdateDTO;
import com.locadora_rdt_backend.modules.customers.model.Customer;
import com.locadora_rdt_backend.modules.customers.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Tag(name = "Customers", description = "Endpoints for customer management")
@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @Operation(summary = "List customers with pagination")
    @GetMapping
    public ResponseEntity<Page<CustomerDTO>> findAllPaged(
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "3") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "name") String orderBy) {

        PageRequest pageRequest = PageRequest.of(
                page,
                linesPerPage,
                Sort.Direction.valueOf(direction),
                orderBy
        );

        Page<CustomerDTO> list = service.findAllPaged(name.trim(), pageRequest);
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Get customer by ID")
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> findById(@PathVariable Long id) {
        CustomerDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Create customer")
    @PostMapping
    public ResponseEntity<CustomerDTO> insert(@Valid @RequestBody CustomerInsertDTO dto) {
        CustomerDTO result = service.insert(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(result.getId())
                .toUri();

        return ResponseEntity.created(uri).body(result);
    }

    @Operation(summary = "Update customer")
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody CustomerUpdateDTO dto) {

        CustomerDTO customerDto = service.update(id, dto);
        return ResponseEntity.ok(customerDto);
    }

    @Operation(summary = "Update customer photo")
    @PutMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updatePhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        service.updatePhoto(id, file);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get customer photo")
    @GetMapping("/{id}/photo")
    public ResponseEntity<byte[]> getPhoto(@PathVariable Long id) {
        Customer entity = service.findEntityById(id);

        if (entity.getPhoto() == null || entity.getPhoto().length == 0) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(entity.getPhotoContentType()))
                .body(entity.getPhoto());
    }

    @Operation(summary = "Delete customer")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete multiple customers")
    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAll(@RequestBody List<Long> ids) {
        service.deleteAll(ids);
        return ResponseEntity.noContent().build();
    }
    @Operation(summary = "Activates and deactivates the customer.")
    @PatchMapping("/{id}/active")
    public ResponseEntity<CustomerDTO> changeActive(@PathVariable Long id, @RequestBody boolean active) {
        service.changeActiveStatus(id, active);
        return ResponseEntity.noContent().build();
    }

}