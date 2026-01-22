package com.locadora_rdt_backend.controllers;

import com.locadora_rdt_backend.dto.PermissionDTO;
import com.locadora_rdt_backend.services.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(value = "/permissions")
public class PermissionController {

    @Autowired
    private PermissionService service;

    @GetMapping
    public ResponseEntity<List<PermissionDTO>> findAll(
            @RequestParam(value = "groupName", defaultValue = "") String groupName
    ) {
        List<PermissionDTO> list = service.findAll(groupName);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/groups")
    public ResponseEntity<List<String>> findAllGroups() {
        List<String> groups = service.findAllGroupNames();
        return ResponseEntity.ok().body(groups);
    }
}
