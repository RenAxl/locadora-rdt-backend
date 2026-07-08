package com.locadora_rdt_backend.modules.reports.financialreports.service;

import java.util.List;
import java.util.Map;

class ReportData {

    private final String title;
    private final List<String> columns;
    private final List<Map<String, ?>> rows;

    ReportData(String title, List<String> columns, List<Map<String, ?>> rows) {
        this.title = title;
        this.columns = columns;
        this.rows = rows;
    }

    String getTitle() {
        return title;
    }

    List<String> getColumns() {
        return columns;
    }

    List<Map<String, ?>> getRows() {
        return rows;
    }
}
