package com.locadora_rdt_backend.tests.modules.reports.financialreports.service;

import com.locadora_rdt_backend.shared.reports.ReportFormat;
import com.locadora_rdt_backend.shared.reports.JasperReportGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

class JasperReportGeneratorTests {

    private final JasperReportGenerator generator = new JasperReportGenerator();

    @Test
    void generateShouldReturnPdfBytes() {
        byte[] result = generator.generate(
                "Teste",
                Arrays.asList("Coluna"),
                List.of(Map.of("column0", "Valor")),
                ReportFormat.PDF
        );

        Assertions.assertTrue(result.length > 4);
        Assertions.assertEquals('%', result[0]);
        Assertions.assertEquals('P', result[1]);
        Assertions.assertEquals('D', result[2]);
        Assertions.assertEquals('F', result[3]);
    }

    @Test
    void generateShouldReturnXlsxBytes() {
        byte[] result = generator.generate(
                "Teste",
                Arrays.asList("Coluna"),
                List.of(Map.of("column0", "Valor")),
                ReportFormat.XLSX
        );

        Assertions.assertTrue(result.length > 4);
        Assertions.assertEquals('P', result[0]);
        Assertions.assertEquals('K', result[1]);
    }
}
