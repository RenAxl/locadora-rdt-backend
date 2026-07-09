package com.locadora_rdt_backend.shared.reports;

public enum ReportFormat {
    PDF,
    XLSX;

    public static ReportFormat from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Formato do relatório não informado.");
        }

        for (ReportFormat format : values()) {
            if (format.name().equalsIgnoreCase(value)) {
                return format;
            }
        }

        throw new IllegalArgumentException("Formato de relatório inválido.");
    }

    public String getExtension() {
        return name().toLowerCase();
    }

    public String getContentType() {
        if (this == PDF) {
            return "application/pdf";
        }

        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    }
}
