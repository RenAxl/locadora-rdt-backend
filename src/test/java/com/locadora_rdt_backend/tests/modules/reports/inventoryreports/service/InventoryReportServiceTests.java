package com.locadora_rdt_backend.tests.modules.reports.inventoryreports.service;

import com.locadora_rdt_backend.modules.inventory.stockbalances.model.StockBalance;
import com.locadora_rdt_backend.modules.inventory.stockmovements.model.StockMovement;
import com.locadora_rdt_backend.shared.reports.ReportFileDTO;
import com.locadora_rdt_backend.shared.reports.ReportFormat;
import com.locadora_rdt_backend.shared.reports.JasperReportGenerator;
import com.locadora_rdt_backend.modules.reports.inventoryreports.dto.InventoryReportFilterDTO;
import com.locadora_rdt_backend.modules.reports.inventoryreports.service.InventoryReportQueryService;
import com.locadora_rdt_backend.modules.reports.inventoryreports.service.InventoryReportServiceImpl;
import com.locadora_rdt_backend.modules.reports.inventoryreports.service.InventoryReportTableService;
import com.locadora_rdt_backend.tests.modules.inventory.stock.factory.StockFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.List;

class InventoryReportServiceTests {

    private InventoryReportQueryService queryService;
    private InventoryReportTableService tableService;
    private JasperReportGenerator generator;
    private InventoryReportServiceImpl service;

    @BeforeEach
    void setUp() {
        queryService = Mockito.mock(InventoryReportQueryService.class);
        tableService = new InventoryReportTableService();
        generator = Mockito.mock(JasperReportGenerator.class);
        service = new InventoryReportServiceImpl(queryService, tableService, generator);

        Mockito.when(queryService.normalize(ArgumentMatchers.any())).thenAnswer(invocation -> {
            InventoryReportFilterDTO filters = invocation.getArgument(0);
            return filters == null ? new InventoryReportFilterDTO() : filters;
        });

        Mockito.when(generator.generate(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyList(),
                ArgumentMatchers.anyList(),
                ArgumentMatchers.any(ReportFormat.class)
        )).thenReturn(new byte[]{1, 2, 3});
    }

    @Test
    void generateCurrentStockShouldReturnPdfFile() {
        Mockito.when(queryService.findCurrentStock(ArgumentMatchers.any()))
                .thenReturn(List.of(StockFactory.createStockBalance()));

        ReportFileDTO file = service.generate("current-stock", "pdf", new InventoryReportFilterDTO());

        Assertions.assertEquals("current_stock.pdf", file.getFileName());
        Assertions.assertEquals("application/pdf", file.getContentType());
        Assertions.assertArrayEquals(new byte[]{1, 2, 3}, file.getContent());
        Mockito.verify(generator).generate(
                ArgumentMatchers.eq("Saldo Atual de Estoque"),
                ArgumentMatchers.anyList(),
                ArgumentMatchers.anyList(),
                ArgumentMatchers.eq(ReportFormat.PDF)
        );
    }

    @Test
    void generateLowStockShouldReturnXlsxFile() {
        Mockito.when(queryService.findLowStock(ArgumentMatchers.any()))
                .thenReturn(List.of(StockFactory.createStockBalance()));

        ReportFileDTO file = service.generate("low-stock", "xlsx", new InventoryReportFilterDTO());

        Assertions.assertEquals("low_stock.xlsx", file.getFileName());
        Assertions.assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", file.getContentType());
        Mockito.verify(generator).generate(
                ArgumentMatchers.eq("Itens com Estoque Baixo"),
                ArgumentMatchers.anyList(),
                ArgumentMatchers.anyList(),
                ArgumentMatchers.eq(ReportFormat.XLSX)
        );
    }

    @Test
    void generateMovementHistoryShouldReturnReport() {
        List<StockMovement> movements = List.of(StockFactory.createStockMovement("ENTRY"));
        Mockito.when(queryService.findMovementHistory(ArgumentMatchers.any())).thenReturn(movements);

        ReportFileDTO file = service.generate("movement-history", "pdf", new InventoryReportFilterDTO());

        Assertions.assertEquals("movement_history.pdf", file.getFileName());
        Mockito.verify(generator).generate(
                ArgumentMatchers.eq("Histórico de Movimentações"),
                ArgumentMatchers.anyList(),
                ArgumentMatchers.anyList(),
                ArgumentMatchers.eq(ReportFormat.PDF)
        );
    }

    @Test
    void generateManualAdjustmentsShouldReturnReport() {
        List<StockMovement> movements = List.of(StockFactory.createStockMovement("ADJUSTMENT"));
        Mockito.when(queryService.findManualAdjustments(ArgumentMatchers.any())).thenReturn(movements);

        ReportFileDTO file = service.generate("manual-adjustments", "pdf", new InventoryReportFilterDTO());

        Assertions.assertEquals("manual_adjustments.pdf", file.getFileName());
        Mockito.verify(generator).generate(
                ArgumentMatchers.eq("Ajustes Manuais de Estoque"),
                ArgumentMatchers.anyList(),
                ArgumentMatchers.anyList(),
                ArgumentMatchers.eq(ReportFormat.PDF)
        );
    }

    @Test
    void generateShouldRejectInvalidValues() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.generate(null, "pdf", new InventoryReportFilterDTO()));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.generate("current-stock", null, new InventoryReportFilterDTO()));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.generate("invalid", "pdf", new InventoryReportFilterDTO()));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.generate("current-stock", "doc", new InventoryReportFilterDTO()));
    }
}
