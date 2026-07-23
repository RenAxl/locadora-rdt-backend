package com.locadora_rdt_backend.modules.organization.employees.controller;

import com.locadora_rdt_backend.modules.organization.employees.dto.EmployeeDTO;
import com.locadora_rdt_backend.modules.organization.employees.dto.EmployeeDetailsDTO;
import com.locadora_rdt_backend.modules.organization.employees.dto.EmployeeInsertDTO;
import com.locadora_rdt_backend.modules.organization.employees.dto.EmployeeUpdateDTO;
import com.locadora_rdt_backend.modules.organization.employees.model.Employee;
import com.locadora_rdt_backend.modules.organization.employees.service.EmployeeService;
import com.locadora_rdt_backend.shared.web.BinaryResponseBuilder;
import com.locadora_rdt_backend.shared.web.ControllerResponseBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<EmployeeDTO>> findAllPaged(
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "3") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "name") String orderBy
    ) {
        PageRequest pageRequest = ControllerResponseBuilder.pageRequest(page, linesPerPage, direction, orderBy);

        Page<EmployeeDTO> list = service.findAllPaged(name.trim(), pageRequest);

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDetailsDTO> findById(@PathVariable Long id) {
        EmployeeDetailsDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<EmployeeDTO> insert(@Valid @RequestBody EmployeeInsertDTO dto) {
        EmployeeDTO result = service.insert(dto);

        return ControllerResponseBuilder.created(result.getId(), result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeUpdateDTO dto
    ) {
        EmployeeDTO result = service.update(id, dto);
        return ResponseEntity.ok(result);
    }

    @PutMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updatePhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        service.updatePhoto(id, file);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/photo")
    public ResponseEntity<byte[]> getPhoto(@PathVariable Long id) {
        Employee entity = service.findEntityById(id);

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
    public ResponseEntity<Void> changeActive(
            @PathVariable Long id,
            @RequestBody boolean active
    ) {
        service.changeActiveStatus(id, active);
        return ResponseEntity.noContent().build();
    }
}
