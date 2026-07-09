package com.locadora_rdt_backend.shared.reports;

import java.util.List;
import java.util.Map;

public class ReportData {

    private final String title;
    private final List<String> columns;
    private final List<Map<String, ?>> rows;

    public ReportData(String title, List<String> columns, List<Map<String, ?>> rows) {
        this.title = title;
        this.columns = columns;
        this.rows = rows;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getColumns() {
        return columns;
    }

    public List<Map<String, ?>> getRows() {
        return rows;
    }
}
