package com.locadora_rdt_backend.modules.identity.activation.controller;

import com.locadora_rdt_backend.modules.identity.activation.service.AccountActivationService;
import com.locadora_rdt_backend.modules.identity.passwordreset.dto.NewPasswordDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/auth")
public class AccountActivationController {

    private final AccountActivationService service;

    public AccountActivationController(
            AccountActivationService service
    ) {
        this.service = service;
    }

    @PostMapping(value = "/activate")
    public ResponseEntity<Void> activateAccount(@RequestParam String token,
            @Valid @RequestBody NewPasswordDTO dto) {

        service.activateAccount(token, dto);

        return ResponseEntity.noContent().build();
    }
}
