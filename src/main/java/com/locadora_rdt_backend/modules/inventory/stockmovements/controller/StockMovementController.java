package com.locadora_rdt_backend.modules.inventory.stockmovements.controller;

import com.locadora_rdt_backend.modules.inventory.stockmovements.dto.StockMovementDTO;
import com.locadora_rdt_backend.modules.inventory.stockmovements.dto.StockMovementInsertDTO;
import com.locadora_rdt_backend.modules.inventory.stockmovements.service.StockMovementService;
import com.locadora_rdt_backend.shared.web.ControllerResponseBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/inventory/stock-movements")
public class StockMovementController {

    private final StockMovementService service;

    public StockMovementController(StockMovementService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<StockMovementDTO>> findAllPaged(
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "10") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "DESC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "createdAt") String orderBy
    ) {
        PageRequest pageRequest = ControllerResponseBuilder.pageRequest(page, linesPerPage, direction, normalizeOrderBy(orderBy));

        Page<StockMovementDTO> list = service.findAllPaged(name.trim(), pageRequest);

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockMovementDTO> findById(@PathVariable Long id) {
        StockMovementDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<StockMovementDTO> insert(@Valid @RequestBody StockMovementInsertDTO dto) {
        StockMovementDTO result = service.insert(dto);

        return ControllerResponseBuilder.created(result.getId(), result);
    }

    private String normalizeOrderBy(String orderBy) {
        return "name".equals(orderBy) ? "item.name" : orderBy;
    }
}
