package com.locadora_rdt_backend.modules.dashboard.controller;

import com.locadora_rdt_backend.modules.dashboard.dto.DashboardSummaryDTO;
import com.locadora_rdt_backend.modules.dashboard.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.locadora_rdt_backend.modules.organization.employees.constants.EmployeeAuthorizationExpressions.EMPLOYEES_READ;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @PreAuthorize(EMPLOYEES_READ)
    @GetMapping
    public ResponseEntity<DashboardSummaryDTO> getSummary() {
        return ResponseEntity.ok(service.getSummary());
    }
}
