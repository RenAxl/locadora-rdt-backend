package com.locadora_rdt_backend.modules.rentals.rentalhistory.controller;

import com.locadora_rdt_backend.modules.rentals.rentalhistory.dto.RentalHistoryDTO;
import com.locadora_rdt_backend.modules.rentals.rentalhistory.service.RentalHistoryService;
import com.locadora_rdt_backend.shared.web.ControllerResponseBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.locadora_rdt_backend.shared.constants.PermissionConstants.RENTALS_HISTORY_READ;

@RestController
@RequestMapping("/rental-history")
public class RentalHistoryController {
    private final RentalHistoryService service;

    public RentalHistoryController(RentalHistoryService service) {
        this.service = service;
    }

    @PreAuthorize(RENTALS_HISTORY_READ)
    @GetMapping
    public ResponseEntity<Page<RentalHistoryDTO>> findCurrentCustomerHistory(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer linesPerPage,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(defaultValue = "rentalDate") String orderBy) {
        PageRequest request = ControllerResponseBuilder.pageRequest(
                page,
                linesPerPage,
                direction,
                normalizeOrderBy(orderBy)
        );
        return ResponseEntity.ok(service.findCurrentCustomerHistory(request));
    }

    private String normalizeOrderBy(String orderBy) {
        if ("rentalDate".equals(orderBy)) {
            return "rental_date";
        }

        return orderBy;
    }
}
