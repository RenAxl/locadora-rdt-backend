package com.locadora_rdt_backend.modules.stocks.stockbalances.controller;

import com.locadora_rdt_backend.modules.stocks.stockbalances.dto.StockBalanceDTO;
import com.locadora_rdt_backend.modules.stocks.stockbalances.dto.StockBalanceDetailsDTO;
import com.locadora_rdt_backend.modules.stocks.stockbalances.dto.StockBalanceMinimumUpdateDTO;
import com.locadora_rdt_backend.modules.stocks.stockbalances.service.StockBalanceService;
import com.locadora_rdt_backend.shared.web.ControllerResponseBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/inventory/stock-balances")
public class StockBalanceController {

    private final StockBalanceService service;

    public StockBalanceController(StockBalanceService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<StockBalanceDTO>> findAllPaged(
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "10") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "item.name") String orderBy
    ) {
        PageRequest pageRequest = ControllerResponseBuilder.pageRequest(page, linesPerPage, direction, normalizeOrderBy(orderBy));

        Page<StockBalanceDTO> list = service.findAllPaged(name.trim(), pageRequest);

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockBalanceDetailsDTO> findById(@PathVariable Long id) {
        StockBalanceDetailsDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<StockBalanceDetailsDTO> findByItemId(@PathVariable Long itemId) {
        StockBalanceDetailsDTO dto = service.findByItemId(itemId);
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{id}/minimum")
    public ResponseEntity<StockBalanceDTO> updateMinimum(
            @PathVariable Long id,
            @Valid @RequestBody StockBalanceMinimumUpdateDTO dto
    ) {
        return ResponseEntity.ok(service.updateMinimum(id, dto.getMinimumQuantity()));
    }

    private String normalizeOrderBy(String orderBy) {
        return "name".equals(orderBy) ? "item.name" : orderBy;
    }
}
