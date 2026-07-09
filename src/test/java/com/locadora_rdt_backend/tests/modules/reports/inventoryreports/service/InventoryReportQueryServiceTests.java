package com.locadora_rdt_backend.tests.modules.reports.inventoryreports.service;

import com.locadora_rdt_backend.modules.inventory.stockbalances.model.StockBalance;
import com.locadora_rdt_backend.modules.inventory.stockmovements.model.StockMovement;
import com.locadora_rdt_backend.modules.reports.inventoryreports.dto.InventoryReportFilterDTO;
import com.locadora_rdt_backend.modules.reports.inventoryreports.repository.InventoryReportStockBalanceRepository;
import com.locadora_rdt_backend.modules.reports.inventoryreports.repository.InventoryReportStockMovementRepository;
import com.locadora_rdt_backend.modules.reports.inventoryreports.service.InventoryReportQueryService;
import com.locadora_rdt_backend.tests.modules.inventory.stock.factory.StockFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;

class InventoryReportQueryServiceTests {

    private InventoryReportStockBalanceRepository stockBalanceRepository;
    private InventoryReportStockMovementRepository stockMovementRepository;
    private InventoryReportQueryService service;

    @BeforeEach
    void setUp() {
        stockBalanceRepository = Mockito.mock(InventoryReportStockBalanceRepository.class);
        stockMovementRepository = Mockito.mock(InventoryReportStockMovementRepository.class);
        service = new InventoryReportQueryService(stockBalanceRepository, stockMovementRepository);
    }

    @Test
    void normalizeShouldCreateDefaultFiltersWhenNull() {
        InventoryReportFilterDTO filters = service.normalize(null);

        Assertions.assertEquals("ALL", filters.getMovementType());
    }

    @Test
    void normalizeShouldFormatMovementType() {
        InventoryReportFilterDTO filters = new InventoryReportFilterDTO();
        filters.setMovementType(" manual-adjustment ");

        InventoryReportFilterDTO result = service.normalize(filters);

        Assertions.assertSame(filters, result);
        Assertions.assertEquals("MANUAL_ADJUSTMENT", result.getMovementType());
    }

    @Test
    void normalizeShouldUseAllWhenMovementTypeIsBlank() {
        InventoryReportFilterDTO filters = new InventoryReportFilterDTO();
        filters.setMovementType(" ");

        InventoryReportFilterDTO result = service.normalize(filters);

        Assertions.assertEquals("ALL", result.getMovementType());
    }

    @Test
    void findCurrentStockShouldCallRepository() {
        InventoryReportFilterDTO filters = new InventoryReportFilterDTO();
        filters.setItemId(10L);
        List<StockBalance> expected = List.of(StockFactory.createStockBalance());
        Mockito.when(stockBalanceRepository.findCurrentStock(10L)).thenReturn(expected);

        List<StockBalance> result = service.findCurrentStock(filters);

        Assertions.assertSame(expected, result);
        Mockito.verify(stockBalanceRepository).findCurrentStock(10L);
    }

    @Test
    void findLowStockShouldUseDisabledItemWhenItemIsNull() {
        InventoryReportFilterDTO filters = new InventoryReportFilterDTO();
        List<StockBalance> expected = List.of(StockFactory.createStockBalance());
        Mockito.when(stockBalanceRepository.findLowStock(-1L)).thenReturn(expected);

        List<StockBalance> result = service.findLowStock(filters);

        Assertions.assertSame(expected, result);
        Mockito.verify(stockBalanceRepository).findLowStock(-1L);
    }

    @Test
    void findMovementHistoryShouldCallRepositoryWithFilters() {
        InventoryReportFilterDTO filters = new InventoryReportFilterDTO();
        filters.setItemId(5L);
        filters.setMovementType("entry");
        filters.setStartDate(LocalDate.of(2026, 1, 1));
        filters.setEndDate(LocalDate.of(2026, 1, 31));
        List<StockMovement> expected = List.of(StockFactory.createStockMovement("ENTRY"));
        Mockito.when(stockMovementRepository.findMovementHistory(
                5L,
                "ENTRY",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31),
                true,
                true
        )).thenReturn(expected);

        List<StockMovement> result = service.findMovementHistory(filters);

        Assertions.assertSame(expected, result);
    }

    @Test
    void findManualAdjustmentsShouldUseDisabledValuesWhenFiltersAreEmpty() {
        InventoryReportFilterDTO filters = new InventoryReportFilterDTO();
        List<StockMovement> expected = List.of(StockFactory.createStockMovement("ADJUSTMENT"));
        Mockito.when(stockMovementRepository.findManualAdjustments(
                -1L,
                LocalDate.of(1970, 1, 1),
                LocalDate.of(1970, 1, 1),
                false,
                false
        )).thenReturn(expected);

        List<StockMovement> result = service.findManualAdjustments(filters);

        Assertions.assertSame(expected, result);
    }

    @Test
    void findMovementHistoryShouldUseDisabledValuesWhenFiltersAreEmpty() {
        InventoryReportFilterDTO filters = new InventoryReportFilterDTO();
        List<StockMovement> expected = List.of(StockFactory.createStockMovement("ENTRY"));
        Mockito.when(stockMovementRepository.findMovementHistory(
                -1L,
                "ALL",
                LocalDate.of(1970, 1, 1),
                LocalDate.of(1970, 1, 1),
                false,
                false
        )).thenReturn(expected);

        List<StockMovement> result = service.findMovementHistory(filters);

        Assertions.assertSame(expected, result);
    }

    @Test
    void findManualAdjustmentsShouldCallRepositoryWithDates() {
        InventoryReportFilterDTO filters = new InventoryReportFilterDTO();
        filters.setItemId(0L);
        filters.setStartDate(LocalDate.of(2026, 2, 1));
        filters.setEndDate(LocalDate.of(2026, 2, 28));
        List<StockMovement> expected = List.of(StockFactory.createStockMovement("ADJUSTMENT"));
        Mockito.when(stockMovementRepository.findManualAdjustments(
                -1L,
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 2, 28),
                true,
                true
        )).thenReturn(expected);

        List<StockMovement> result = service.findManualAdjustments(filters);

        Assertions.assertSame(expected, result);
    }
}
