package com.locadora_rdt_backend.modules.reports.inventoryreports.service;

import com.locadora_rdt_backend.modules.inventory.stockbalances.model.StockBalance;
import com.locadora_rdt_backend.modules.inventory.stockmovements.model.StockMovement;
import com.locadora_rdt_backend.shared.reports.ReportData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.locadora_rdt_backend.shared.reports.ReportTableSupport.dateTime;
import static com.locadora_rdt_backend.shared.reports.ReportTableSupport.number;
import static com.locadora_rdt_backend.shared.reports.ReportTableSupport.row;
import static com.locadora_rdt_backend.shared.reports.ReportTableSupport.text;
import static com.locadora_rdt_backend.shared.reports.ReportTableSupport.valueOrZero;

@Service
public class InventoryReportTableService {

    public ReportData currentStockReport(List<StockBalance> items) {
        List<String> columns = Arrays.asList("Item", "Total", "Disponível", "Reservado", "Indisponível", "Mínimo", "Alerta");
        List<Map<String, ?>> rows = new ArrayList<>();

        for (StockBalance item : items) {
            Integer available = availableQuantity(item);
            rows.add(row(
                    itemName(item),
                    number(item.getTotalQuantity()),
                    number(available),
                    number(item.getReservedQuantity()),
                    number(item.getUnavailableQuantity()),
                    number(item.getMinimumQuantity()),
                    available <= valueOrZero(item.getMinimumQuantity()) ? "Estoque baixo" : ""
            ));
        }

        return new ReportData("Saldo Atual de Estoque", columns, rows);
    }

    public ReportData lowStockReport(List<StockBalance> items) {
        List<String> columns = Arrays.asList("Item", "Disponível", "Mínimo", "Total", "Reservado", "Indisponível");
        List<Map<String, ?>> rows = new ArrayList<>();

        for (StockBalance item : items) {
            rows.add(row(
                    itemName(item),
                    number(availableQuantity(item)),
                    number(item.getMinimumQuantity()),
                    number(item.getTotalQuantity()),
                    number(item.getReservedQuantity()),
                    number(item.getUnavailableQuantity())
            ));
        }

        return new ReportData("Itens com Estoque Baixo", columns, rows);
    }

    public ReportData movementHistoryReport(List<StockMovement> items) {
        List<String> columns = Arrays.asList("Data", "Item", "Tipo", "Quantidade", "Motivo", "Referência", "Usuário");
        return movementReport("Histórico de Movimentações", columns, items);
    }

    public ReportData manualAdjustmentsReport(List<StockMovement> items) {
        List<String> columns = Arrays.asList("Data", "Item", "Quantidade", "Motivo", "Referência", "Usuário");
        List<Map<String, ?>> rows = new ArrayList<>();

        for (StockMovement item : items) {
            rows.add(row(
                    dateTime(item.getCreatedAt()),
                    itemName(item),
                    number(item.getQuantity()),
                    text(item.getReason()),
                    reference(item),
                    text(item.getCreatedBy())
            ));
        }

        return new ReportData("Ajustes Manuais de Estoque", columns, rows);
    }

    private ReportData movementReport(String title, List<String> columns, List<StockMovement> items) {
        List<Map<String, ?>> rows = new ArrayList<>();

        for (StockMovement item : items) {
            rows.add(row(
                    dateTime(item.getCreatedAt()),
                    itemName(item),
                    movementType(item.getType()),
                    number(item.getQuantity()),
                    text(item.getReason()),
                    reference(item),
                    text(item.getCreatedBy())
            ));
        }

        return new ReportData(title, columns, rows);
    }

    private String itemName(StockBalance item) {
        if (item.getItem() == null) {
            return "";
        }

        return text(item.getItem().getName());
    }

    private String itemName(StockMovement item) {
        if (item.getItem() == null) {
            return "";
        }

        return text(item.getItem().getName());
    }

    private Integer availableQuantity(StockBalance item) {
        return valueOrZero(item.getTotalQuantity())
                - valueOrZero(item.getReservedQuantity())
                - valueOrZero(item.getUnavailableQuantity());
    }

    private String reference(StockMovement item) {
        if (item.getReferenceType() == null || item.getReferenceType().trim().isEmpty()) {
            return "";
        }

        if (item.getReferenceId() == null) {
            return item.getReferenceType();
        }

        return item.getReferenceType() + " #" + item.getReferenceId();
    }

    private String movementType(String type) {
        if ("ENTRY".equals(type)) {
            return "Entrada";
        }
        if ("EXIT".equals(type)) {
            return "Saída";
        }
        if ("RESERVE".equals(type)) {
            return "Reserva";
        }
        if ("RETURN".equals(type)) {
            return "Devolução";
        }
        if ("ADJUSTMENT".equals(type)) {
            return "Ajuste";
        }

        return text(type);
    }

}
