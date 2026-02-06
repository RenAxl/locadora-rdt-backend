package com.locadora_rdt_backend.controllers;

import com.locadora_rdt_backend.dto.RoleDTO;
import com.locadora_rdt_backend.dto.RoleListDTO;
import com.locadora_rdt_backend.dto.RolePermissionsUpdateDTO;
import com.locadora_rdt_backend.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping(value = "/roles")
public class RoleController {

    @Autowired
    private RoleService service;

    @GetMapping
    public ResponseEntity<Page<RoleListDTO>> findAllPaged(
            @RequestParam(value = "authority", defaultValue = "") String authority,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "12") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "authority") String orderBy
    ) {
        PageRequest pageRequest =
                PageRequest.of(page, linesPerPage, Sort.Direction.valueOf(direction), orderBy);

        Page<RoleListDTO> list = service.findAllPaged(authority.trim(), pageRequest);

        return ResponseEntity.ok().body(list);
    }


    @GetMapping(value = "/{id}")
    public ResponseEntity<RoleDTO> findById(@PathVariable Long id) {
        RoleDTO dto = service.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    @PutMapping(value = "/{id}/permissions")
    public ResponseEntity<RoleDTO> updatePermissions(
            @PathVariable Long id,
            @Valid @RequestBody RolePermissionsUpdateDTO dto
    ) {
        RoleDTO updated = service.updateRolePermissions(id, dto);
        return ResponseEntity.ok().body(updated);
    }


    @PostMapping
    public ResponseEntity<RoleDTO> insert(@Valid @RequestBody RoleDTO dto) {
        RoleDTO created = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(uri).body(created);
    }
}
