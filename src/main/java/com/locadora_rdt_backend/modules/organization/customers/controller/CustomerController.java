package com.locadora_rdt_backend.modules.organization.customers.controller;

import com.locadora_rdt_backend.modules.organization.customers.dto.CustomerDTO;
import com.locadora_rdt_backend.modules.organization.customers.dto.CustomerDetailsDTO;
import com.locadora_rdt_backend.modules.organization.customers.dto.CustomerInsertDTO;
import com.locadora_rdt_backend.modules.organization.customers.dto.CustomerUpdateDTO;
import com.locadora_rdt_backend.modules.organization.customers.model.Customer;
import com.locadora_rdt_backend.modules.organization.customers.service.CustomerService;
import com.locadora_rdt_backend.shared.web.BinaryResponseBuilder;
import com.locadora_rdt_backend.shared.web.ControllerResponseBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Customers", description = "Endpoints for customer management")
@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<CustomerDTO>> findAllPaged(
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "3") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "name") String orderBy) {

        PageRequest pageRequest = ControllerResponseBuilder.pageRequest(page, linesPerPage, direction, orderBy);

        Page<CustomerDTO> list = service.findAllPaged(name.trim(), pageRequest);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDetailsDTO> findById(@PathVariable Long id) {
        CustomerDetailsDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }


    @PostMapping
    public ResponseEntity<CustomerDTO> insert(@Valid @RequestBody CustomerInsertDTO dto) {
        CustomerDTO result = service.insert(dto);

        return ControllerResponseBuilder.created(result.getId(), result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody CustomerUpdateDTO dto) {

        CustomerDTO customerDto = service.update(id, dto);
        return ResponseEntity.ok(customerDto);
    }

    @PutMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updatePhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        service.updatePhoto(id, file);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/photo")
    public ResponseEntity<byte[]> getPhoto(@PathVariable Long id) {
        Customer entity = service.findEntityById(id);

        return BinaryResponseBuilder.media(entity.getPhoto(), entity.getPhotoContentType());
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
    public ResponseEntity<Void> changeActive(@PathVariable Long id, @RequestBody boolean active) {
        service.changeActiveStatus(id, active);
        return ResponseEntity.noContent().build();
    }

}
