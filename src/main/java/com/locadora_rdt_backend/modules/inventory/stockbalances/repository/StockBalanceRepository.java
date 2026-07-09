package com.locadora_rdt_backend.modules.inventory.stockbalances.repository;

import com.locadora_rdt_backend.modules.inventory.stockbalances.model.StockBalance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockBalanceRepository extends JpaRepository<StockBalance, Long> {

    @Query("select balance from StockBalance balance where lower(balance.item.name) like lower(concat('%', ?1, '%'))")
    Page<StockBalance> find(String name, Pageable pageable);

    Optional<StockBalance> findByItemId(Long itemId);
}
