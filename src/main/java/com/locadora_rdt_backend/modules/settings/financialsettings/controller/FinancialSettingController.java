package com.locadora_rdt_backend.modules.settings.financialsettings.controller;

import com.locadora_rdt_backend.modules.settings.financialsettings.dto.FinancialSettingDTO;
import com.locadora_rdt_backend.modules.settings.financialsettings.dto.FinancialSettingUpdateDTO;
import com.locadora_rdt_backend.modules.settings.financialsettings.service.FinancialSettingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.locadora_rdt_backend.modules.settings.financialsettings.constants.FinancialSettingsAuthorizationExpressions.FINANCIALSETTINGS_READ;
import static com.locadora_rdt_backend.modules.settings.financialsettings.constants.FinancialSettingsAuthorizationExpressions.FINANCIALSETTINGS_WRITE;

@RestController
@RequestMapping("/financial-settings")
public class FinancialSettingController {

    private final FinancialSettingService service;

    public FinancialSettingController(FinancialSettingService service) {
        this.service = service;
    }

    @PreAuthorize(FINANCIALSETTINGS_READ)
    @GetMapping
    public ResponseEntity<FinancialSettingDTO> findCurrent() {
        FinancialSettingDTO dto = service.findCurrent();
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize(FINANCIALSETTINGS_WRITE)
    @PutMapping
    public ResponseEntity<FinancialSettingDTO> update(
            @Valid @RequestBody FinancialSettingUpdateDTO dto
    ) {
        FinancialSettingDTO result = service.update(dto);
        return ResponseEntity.ok(result);
    }
}
