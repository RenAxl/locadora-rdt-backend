package com.locadora_rdt_backend.modules.rental.repository;

import com.locadora_rdt_backend.modules.rental.model.RentalItemUnit;
import com.locadora_rdt_backend.modules.rental.model.RentalItemUnitStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalItemUnitRepository extends JpaRepository<RentalItemUnit, Long> {

    List<RentalItemUnit> findByRentalItemRentalIdOrderById(Long rentalId);

    long countByRentalItemIdAndStatusIn(Long rentalItemId, List<RentalItemUnitStatus> statuses);

    boolean existsByItemUnitIdAndStatusIn(Long itemUnitId, List<RentalItemUnitStatus> statuses);
}
