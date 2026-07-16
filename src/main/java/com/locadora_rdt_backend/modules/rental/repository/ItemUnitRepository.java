package com.locadora_rdt_backend.modules.rental.repository;

import com.locadora_rdt_backend.modules.rental.model.ItemUnit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;

public interface ItemUnitRepository extends JpaRepository<ItemUnit, Long> {

    long countByItemIdAndStatusAndActiveTrue(Long itemId, String status);

    List<ItemUnit> findByItemIdAndStatusAndActiveTrueOrderByAssetCode(Long itemId, String status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT unit FROM ItemUnit unit "
            + "WHERE unit.item.id = :itemId "
            + "AND unit.status = 'AVAILABLE' "
            + "AND unit.active = true "
            + "ORDER BY unit.id")
    List<ItemUnit> findAvailableForReservation(@Param("itemId") Long itemId, Pageable pageable);
}
