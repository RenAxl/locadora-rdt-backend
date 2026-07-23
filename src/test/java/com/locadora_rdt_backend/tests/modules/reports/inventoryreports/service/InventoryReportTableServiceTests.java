package com.locadora_rdt_backend.tests.modules.reports.inventoryreports.service;

import com.locadora_rdt_backend.modules.stocks.stockbalances.model.StockBalance;
import com.locadora_rdt_backend.modules.stocks.stockmovements.model.StockMovement;
import com.locadora_rdt_backend.modules.reports.inventoryreports.service.InventoryReportTableService;
import com.locadora_rdt_backend.tests.modules.inventory.stock.factory.StockFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

class InventoryReportTableServiceTests {

    private final InventoryReportTableService service = new InventoryReportTableService();

    @Test
    void currentStockReportShouldCreateRows() throws Exception {
        StockBalance balance = StockFactory.createStockBalance();

        Object report = service.currentStockReport(List.of(balance));

        Assertions.assertEquals("Saldo Atual de Estoque", title(report));
        Assertions.assertEquals("Playstation 5", rows(report).get(0).get("column0"));
        Assertions.assertEquals("10", rows(report).get(0).get("column1"));
        Assertions.assertEquals("7", rows(report).get(0).get("column2"));
        Assertions.assertEquals("", rows(report).get(0).get("column6"));
    }

    @Test
    void currentStockReportShouldShowLowStockAlert() throws Exception {
        StockBalance balance = StockFactory.createStockBalance();
        balance.setTotalQuantity(4);
        balance.setReservedQuantity(1);
        balance.setUnavailableQuantity(1);
        balance.setMinimumQuantity(3);

        Object report = service.currentStockReport(List.of(balance));

        Assertions.assertEquals("Estoque baixo", rows(report).get(0).get("column6"));
    }

    @Test
    void lowStockReportShouldCreateRows() throws Exception {
        StockBalance balance = StockFactory.createStockBalance();

        Object report = service.lowStockReport(List.of(balance));

        Assertions.assertEquals("Itens com Estoque Baixo", title(report));
        Assertions.assertEquals("7", rows(report).get(0).get("column1"));
        Assertions.assertEquals("3", rows(report).get(0).get("column2"));
    }

    @Test
    void movementHistoryReportShouldCreateRows() throws Exception {
        StockMovement movement = StockFactory.createStockMovement("ENTRY");

        Object report = service.movementHistoryReport(List.of(movement));

        Assertions.assertEquals("Histórico de Movimentações", title(report));
        Assertions.assertEquals("Entrada", rows(report).get(0).get("column2"));
        Assertions.assertEquals("TEST #10", rows(report).get(0).get("column5"));
    }

    @Test
    void movementHistoryReportShouldTranslateAllTypes() throws Exception {
        Assertions.assertEquals("Saída", rows(service.movementHistoryReport(
                List.of(StockFactory.createStockMovement("EXIT")))).get(0).get("column2"));
        Assertions.assertEquals("Reserva", rows(service.movementHistoryReport(
                List.of(StockFactory.createStockMovement("RESERVE")))).get(0).get("column2"));
        Assertions.assertEquals("Devolução", rows(service.movementHistoryReport(
                List.of(StockFactory.createStockMovement("RETURN")))).get(0).get("column2"));
        Assertions.assertEquals("Ajuste", rows(service.movementHistoryReport(
                List.of(StockFactory.createStockMovement("ADJUSTMENT")))).get(0).get("column2"));
        Assertions.assertEquals("OTHER", rows(service.movementHistoryReport(
                List.of(StockFactory.createStockMovement("OTHER")))).get(0).get("column2"));
    }

    @Test
    void manualAdjustmentsReportShouldCreateRows() throws Exception {
        StockMovement movement = StockFactory.createStockMovement("ADJUSTMENT");

        Object report = service.manualAdjustmentsReport(List.of(movement));

        Assertions.assertEquals("Ajustes Manuais de Estoque", title(report));
        Assertions.assertEquals("2", rows(report).get(0).get("column2"));
        Assertions.assertEquals("Ajuste de teste", rows(report).get(0).get("column3"));
    }

    @Test
    void reportsShouldUseEmptyTextWhenValuesAreNull() throws Exception {
        StockBalance balance = StockFactory.createStockBalance();
        balance.setItem(null);
        balance.setTotalQuantity(null);
        balance.setReservedQuantity(null);
        balance.setUnavailableQuantity(null);
        balance.setMinimumQuantity(null);
        StockMovement movement = StockFactory.createStockMovement("ENTRY");
        movement.setItem(null);
        movement.setCreatedAt(null);
        movement.setReason(null);
        movement.setReferenceType(null);
        movement.setCreatedBy(null);

        Object stockReport = service.currentStockReport(List.of(balance));
        Object movementReport = service.manualAdjustmentsReport(List.of(movement));

        Assertions.assertEquals("", rows(stockReport).get(0).get("column0"));
        Assertions.assertEquals("0", rows(stockReport).get(0).get("column1"));
        Assertions.assertEquals("", rows(movementReport).get(0).get("column0"));
        Assertions.assertEquals("", rows(movementReport).get(0).get("column1"));
        Assertions.assertEquals("", rows(movementReport).get(0).get("column3"));
        Assertions.assertEquals("", rows(movementReport).get(0).get("column4"));
    }

    @Test
    void manualAdjustmentsReportShouldShowReferenceTypeWhenReferenceIdIsNull() throws Exception {
        StockMovement movement = StockFactory.createStockMovement("ADJUSTMENT");
        movement.setReferenceId(null);

        Object report = service.manualAdjustmentsReport(List.of(movement));

        Assertions.assertEquals("TEST", rows(report).get(0).get("column4"));
    }

    @Test
    void manualAdjustmentsReportShouldUseEmptyReferenceWhenReferenceTypeIsBlank() throws Exception {
        StockMovement movement = StockFactory.createStockMovement("ADJUSTMENT");
        movement.setReferenceType(" ");

        Object report = service.manualAdjustmentsReport(List.of(movement));

        Assertions.assertEquals("", rows(report).get(0).get("column4"));
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, ?>> rows(Object report) throws Exception {
        Method method = report.getClass().getDeclaredMethod("getRows");
        method.setAccessible(true);
        return (List<Map<String, ?>>) method.invoke(report);
    }

    private String title(Object report) throws Exception {
        Method method = report.getClass().getDeclaredMethod("getTitle");
        method.setAccessible(true);
        return (String) method.invoke(report);
    }
}
