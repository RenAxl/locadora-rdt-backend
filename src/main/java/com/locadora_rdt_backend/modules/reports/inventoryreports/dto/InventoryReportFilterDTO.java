package com.locadora_rdt_backend.modules.reports.inventoryreports.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;

public class InventoryReportFilterDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    private Long itemId;
    private String movementType;

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Long getItemId() {
        return itemId;
    }

    public String getMovementType() {
        return movementType;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }
}
