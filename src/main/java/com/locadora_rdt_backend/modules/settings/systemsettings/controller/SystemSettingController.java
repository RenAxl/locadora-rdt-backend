package com.locadora_rdt_backend.modules.settings.systemsettings.controller;

import com.locadora_rdt_backend.modules.settings.systemsettings.dto.SystemSettingDTO;
import com.locadora_rdt_backend.modules.settings.systemsettings.service.SystemSettingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

@RestController
@RequestMapping("/system-settings")
public class SystemSettingController {
    private final SystemSettingService service;

    public SystemSettingController(SystemSettingService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<SystemSettingDTO> findCurrent() {
        return ResponseEntity.ok(service.findCurrent());
    }

    @PutMapping
    public ResponseEntity<SystemSettingDTO> update(@Valid @RequestBody SystemSettingDTO dto) {
        return ResponseEntity.ok(service.update(dto));
    }
}
