package com.locadora_rdt_backend.modules.stocks.stockmovements.repository;

import com.locadora_rdt_backend.modules.stocks.stockmovements.model.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    @Query("select movement from StockMovement movement where lower(movement.item.name) like lower(concat('%', ?1, '%'))")
    Page<StockMovement> find(String name, Pageable pageable);
}
