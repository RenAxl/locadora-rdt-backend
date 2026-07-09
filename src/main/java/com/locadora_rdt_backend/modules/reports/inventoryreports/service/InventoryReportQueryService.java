package com.locadora_rdt_backend.modules.reports.inventoryreports.service;

import com.locadora_rdt_backend.modules.inventory.stockbalances.model.StockBalance;
import com.locadora_rdt_backend.modules.inventory.stockmovements.model.StockMovement;
import com.locadora_rdt_backend.modules.reports.inventoryreports.dto.InventoryReportFilterDTO;
import com.locadora_rdt_backend.modules.reports.inventoryreports.repository.InventoryReportStockBalanceRepository;
import com.locadora_rdt_backend.modules.reports.inventoryreports.repository.InventoryReportStockMovementRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class InventoryReportQueryService {

    private static final LocalDate FILTER_DATE_DISABLED = LocalDate.of(1970, 1, 1);
    private static final long FILTER_ID_DISABLED = -1L;

    private final InventoryReportStockBalanceRepository stockBalanceRepository;
    private final InventoryReportStockMovementRepository stockMovementRepository;

    public InventoryReportQueryService(
            InventoryReportStockBalanceRepository stockBalanceRepository,
            InventoryReportStockMovementRepository stockMovementRepository
    ) {
        this.stockBalanceRepository = stockBalanceRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    public List<StockBalance> findCurrentStock(InventoryReportFilterDTO filters) {
        return stockBalanceRepository.findCurrentStock(
                idFilterOrDisabled(filters.getItemId())
        );
    }

    public List<StockBalance> findLowStock(InventoryReportFilterDTO filters) {
        return stockBalanceRepository.findLowStock(
                idFilterOrDisabled(filters.getItemId())
        );
    }

    public List<StockMovement> findMovementHistory(InventoryReportFilterDTO filters) {
        return stockMovementRepository.findMovementHistory(
                idFilterOrDisabled(filters.getItemId()),
                normalizeMovementType(filters.getMovementType()),
                dateFilterOrDisabled(filters.getStartDate()),
                dateFilterOrDisabled(filters.getEndDate()),
                filters.getStartDate() != null,
                filters.getEndDate() != null
        );
    }

    public List<StockMovement> findManualAdjustments(InventoryReportFilterDTO filters) {
        return stockMovementRepository.findManualAdjustments(
                idFilterOrDisabled(filters.getItemId()),
                dateFilterOrDisabled(filters.getStartDate()),
                dateFilterOrDisabled(filters.getEndDate()),
                filters.getStartDate() != null,
                filters.getEndDate() != null
        );
    }

    public InventoryReportFilterDTO normalize(InventoryReportFilterDTO filters) {
        InventoryReportFilterDTO normalized = filters == null ? new InventoryReportFilterDTO() : filters;
        normalized.setMovementType(normalizeMovementType(normalized.getMovementType()));
        return normalized;
    }

    private String normalizeMovementType(String movementType) {
        if (movementType == null || movementType.trim().isEmpty()) {
            return "ALL";
        }

        return movementType.trim().replace("-", "_").toUpperCase();
    }

    private Long idFilterOrDisabled(Long id) {
        if (id == null || id <= 0) {
            return FILTER_ID_DISABLED;
        }

        return id;
    }

    private LocalDate dateFilterOrDisabled(LocalDate date) {
        if (date == null) {
            return FILTER_DATE_DISABLED;
        }

        return date;
    }
}
