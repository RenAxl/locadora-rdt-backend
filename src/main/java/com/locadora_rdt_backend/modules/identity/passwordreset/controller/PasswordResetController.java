package com.locadora_rdt_backend.modules.identity.passwordreset.controller;

import com.locadora_rdt_backend.modules.identity.passwordreset.dto.ForgotPasswordDTO;
import com.locadora_rdt_backend.modules.identity.passwordreset.dto.NewPasswordDTO;
import com.locadora_rdt_backend.modules.identity.passwordreset.service.PasswordResetService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/auth")
public class PasswordResetController {

    private final PasswordResetService service;

    public PasswordResetController(
            PasswordResetService service
    ) {
        this.service = service;
    }

    @PostMapping(value = "/forgot-password")
    public ResponseEntity<Void> forgotPassword(
            @Valid @RequestBody ForgotPasswordDTO dto
    ) {

        service.requestPasswordReset(dto);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/reset-password")
    public ResponseEntity<Void> resetPassword(
            @RequestParam String token,
            @Valid @RequestBody NewPasswordDTO dto
    ) {

        service.resetPassword(token, dto);

        return ResponseEntity.noContent().build();
    }
}