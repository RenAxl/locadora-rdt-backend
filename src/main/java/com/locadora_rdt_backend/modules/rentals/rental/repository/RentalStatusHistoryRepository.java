package com.locadora_rdt_backend.modules.rentals.rental.repository;

import com.locadora_rdt_backend.modules.rentals.rental.model.RentalStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalStatusHistoryRepository extends JpaRepository<RentalStatusHistory, Long> {

    List<RentalStatusHistory> findByRentalIdOrderByChangedAtAsc(Long rentalId);

    void deleteByRentalId(Long rentalId);
}
