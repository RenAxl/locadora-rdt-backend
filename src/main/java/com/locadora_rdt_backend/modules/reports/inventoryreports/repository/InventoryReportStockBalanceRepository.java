package com.locadora_rdt_backend.modules.reports.inventoryreports.repository;

import com.locadora_rdt_backend.modules.stocks.stockbalances.model.StockBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InventoryReportStockBalanceRepository extends JpaRepository<StockBalance, Long> {

    @Query(
            value = "SELECT b.* FROM tb_stock_balance b "
                    + "INNER JOIN tb_item i ON i.id = b.item_id "
                    + "WHERE (:itemId <= 0 OR b.item_id = :itemId) "
                    + "ORDER BY i.name ASC",
            nativeQuery = true
    )
    List<StockBalance> findCurrentStock(
            @Param("itemId") Long itemId
    );

    @Query(
            value = "SELECT b.* FROM tb_stock_balance b "
                    + "INNER JOIN tb_item i ON i.id = b.item_id "
                    + "WHERE (:itemId <= 0 OR b.item_id = :itemId) "
                    + "AND ((b.total_quantity - b.reserved_quantity - b.unavailable_quantity) <= b.minimum_quantity) "
                    + "ORDER BY i.name ASC",
            nativeQuery = true
    )
    List<StockBalance> findLowStock(
            @Param("itemId") Long itemId
    );
}
