package com.locadora_rdt_backend.modules.receivables.controller;

import com.locadora_rdt_backend.modules.receivables.dto.ReceivableDTO;
import com.locadora_rdt_backend.modules.receivables.dto.ReceivableInsertDTO;
import com.locadora_rdt_backend.modules.receivables.service.ReceivableService;
import com.locadora_rdt_backend.shared.web.ControllerResponseBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "Receivables", description = "Endpoints for accounts receivable management")
@RestController
@RequestMapping({"/receivables", "/receber"})
public class ReceivableController {

    private final ReceivableService service;

    public ReceivableController(ReceivableService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ReceivableDTO> insert(@Valid @RequestBody ReceivableInsertDTO dto) {
        ReceivableDTO result = service.insert(dto);

        return ControllerResponseBuilder.created(result.getId(), result);
    }
}
