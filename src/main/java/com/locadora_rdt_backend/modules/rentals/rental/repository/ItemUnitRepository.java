package com.locadora_rdt_backend.modules.rentals.rental.repository;

import com.locadora_rdt_backend.modules.rentals.rental.model.ItemUnit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;

public interface ItemUnitRepository extends JpaRepository<ItemUnit, Long> {

    @Query("SELECT COUNT(unit) FROM ItemUnit unit WHERE unit.active = true "
            + "AND unit.status = 'AVAILABLE' "
            + "AND LOWER(unit.item.category.name) LIKE '%jogo%'")
    long countAvailableGames();

    @Query("SELECT COUNT(unit) FROM ItemUnit unit WHERE unit.active = true "
            + "AND LOWER(unit.item.category.name) LIKE '%console%'")
    long countActiveConsoles();

    long countByItemIdAndStatusAndActiveTrue(Long itemId, String status);

    long countByItemIdAndActiveTrue(Long itemId);

    @Query("SELECT COUNT(unit) FROM ItemUnit unit "
            + "WHERE unit.item.id = :itemId "
            + "AND unit.active = true "
            + "AND unit.status NOT IN ('AVAILABLE', 'RESERVED')")
    long countUnavailableByItemId(@Param("itemId") Long itemId);

    List<ItemUnit> findByItemIdAndStatusAndActiveTrueOrderByAssetCode(Long itemId, String status);

    @Query("SELECT COUNT(unit) FROM ItemUnit unit "
            + "WHERE unit.item.id = :itemId "
            + "AND unit.status = 'AVAILABLE' "
            + "AND unit.active = true "
            + "AND NOT EXISTS (SELECT link.id FROM RentalItemUnit link "
            + "WHERE link.itemUnit = unit "
            + "AND link.status IN ("
            + "com.locadora_rdt_backend.modules.rentals.rental.model.RentalItemUnitStatus.RESERVED, "
            + "com.locadora_rdt_backend.modules.rentals.rental.model.RentalItemUnitStatus.DELIVERED))")
    long countAvailableForRental(@Param("itemId") Long itemId);

    @Query("SELECT unit FROM ItemUnit unit "
            + "WHERE unit.item.id = :itemId "
            + "AND unit.status = 'AVAILABLE' "
            + "AND unit.active = true "
            + "AND NOT EXISTS (SELECT link.id FROM RentalItemUnit link "
            + "WHERE link.itemUnit = unit "
            + "AND link.status IN ("
            + "com.locadora_rdt_backend.modules.rentals.rental.model.RentalItemUnitStatus.RESERVED, "
            + "com.locadora_rdt_backend.modules.rentals.rental.model.RentalItemUnitStatus.DELIVERED)) "
            + "ORDER BY unit.assetCode")
    List<ItemUnit> findAvailableForRental(@Param("itemId") Long itemId);

    List<ItemUnit> findByItemIdOrderByAssetCode(Long itemId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT unit FROM ItemUnit unit WHERE unit.item.id = :itemId AND unit.active = true ORDER BY unit.id")
    List<ItemUnit> findActiveByItemIdForUpdate(@Param("itemId") Long itemId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT unit FROM ItemUnit unit "
            + "WHERE unit.item.id = :itemId "
            + "AND unit.status = 'AVAILABLE' "
            + "AND unit.active = true "
            + "AND NOT EXISTS (SELECT link.id FROM RentalItemUnit link "
            + "WHERE link.itemUnit = unit "
            + "AND link.status IN ("
            + "com.locadora_rdt_backend.modules.rentals.rental.model.RentalItemUnitStatus.RESERVED, "
            + "com.locadora_rdt_backend.modules.rentals.rental.model.RentalItemUnitStatus.DELIVERED)) "
            + "ORDER BY unit.id")
    List<ItemUnit> findAvailableForReservation(@Param("itemId") Long itemId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT unit FROM ItemUnit unit "
            + "WHERE unit.item.id = :itemId "
            + "AND unit.status = :status "
            + "AND unit.active = true "
            + "AND NOT EXISTS (SELECT link.id FROM RentalItemUnit link "
            + "WHERE link.itemUnit = unit "
            + "AND link.status IN ("
            + "com.locadora_rdt_backend.modules.rentals.rental.model.RentalItemUnitStatus.RESERVED, "
            + "com.locadora_rdt_backend.modules.rentals.rental.model.RentalItemUnitStatus.DELIVERED)) "
            + "ORDER BY unit.id")
    List<ItemUnit> findByStatusForUpdate(@Param("itemId") Long itemId,
            @Param("status") String status, Pageable pageable);
}
