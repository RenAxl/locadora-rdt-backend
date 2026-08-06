package com.locadora_rdt_backend.modules.dashboard.dto;

import java.time.LocalDate;

public class DashboardDailyRentalDTO {
    private LocalDate date;
    private String label;
    private Long quantity;

    public DashboardDailyRentalDTO(LocalDate date, String label, Long quantity) {
        this.date = date;
        this.label = label;
        this.quantity = quantity;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getLabel() {
        return label;
    }

    public Long getQuantity() {
        return quantity;
    }
}
