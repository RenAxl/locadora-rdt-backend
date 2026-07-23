package com.locadora_rdt_backend.modules.reports.inventoryreports.repository;

import com.locadora_rdt_backend.modules.stocks.stockmovements.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface InventoryReportStockMovementRepository extends JpaRepository<StockMovement, Long> {

    @Query(
            value = "SELECT m.* FROM tb_stock_movement m "
                    + "INNER JOIN tb_item i ON i.id = m.item_id "
                    + "WHERE (:itemId <= 0 OR m.item_id = :itemId) "
                    + "AND (:movementType = 'ALL' OR m.type = :movementType) "
                    + "AND (:hasStartDate = FALSE OR CAST(m.created_at AS DATE) >= :startDate) "
                    + "AND (:hasEndDate = FALSE OR CAST(m.created_at AS DATE) <= :endDate) "
                    + "ORDER BY m.created_at DESC",
            nativeQuery = true
    )
    List<StockMovement> findMovementHistory(
            @Param("itemId") Long itemId,
            @Param("movementType") String movementType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("hasStartDate") Boolean hasStartDate,
            @Param("hasEndDate") Boolean hasEndDate
    );

    @Query(
            value = "SELECT m.* FROM tb_stock_movement m "
                    + "INNER JOIN tb_item i ON i.id = m.item_id "
                    + "WHERE (:itemId <= 0 OR m.item_id = :itemId) "
                    + "AND m.type = 'ADJUSTMENT' "
                    + "AND (:hasStartDate = FALSE OR CAST(m.created_at AS DATE) >= :startDate) "
                    + "AND (:hasEndDate = FALSE OR CAST(m.created_at AS DATE) <= :endDate) "
                    + "ORDER BY m.created_at DESC",
            nativeQuery = true
    )
    List<StockMovement> findManualAdjustments(
            @Param("itemId") Long itemId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("hasStartDate") Boolean hasStartDate,
            @Param("hasEndDate") Boolean hasEndDate
    );
}
