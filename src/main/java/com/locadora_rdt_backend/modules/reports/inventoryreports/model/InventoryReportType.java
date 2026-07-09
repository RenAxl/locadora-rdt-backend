package com.locadora_rdt_backend.modules.reports.inventoryreports.model;

public enum InventoryReportType {
    CURRENT_STOCK,
    LOW_STOCK,
    MOVEMENT_HISTORY,
    MANUAL_ADJUSTMENTS;

    public static InventoryReportType from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Tipo do relatório não informado.");
        }

        String normalized = value.trim().replace("-", "_").toUpperCase();

        for (InventoryReportType type : values()) {
            if (type.name().equals(normalized)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Tipo de relatório inválido.");
    }
}
