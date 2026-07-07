package com.locadora_rdt_backend.modules.reports.model;

public enum ReportType {
    RECEIVABLES,
    PAYABLES,
    FINANCIAL,
    SUMMARY_CUSTOMER,
    SUMMARY_SUPPLIER,
    SUMMARY_EMPLOYEE,
    ANNUAL_BALANCE;

    public static ReportType from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Tipo do relatório não informado.");
        }

        String normalized = value.trim().replace("-", "_").toUpperCase();

        for (ReportType type : values()) {
            if (type.name().equals(normalized)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Tipo de relatório inválido.");
    }
}
