package com.locadora_rdt_backend.modules.rental.repository;

import com.locadora_rdt_backend.modules.rental.model.RentalItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RentalItemRepository extends JpaRepository<RentalItem, Long> {
    List<RentalItem> findByRentalIdOrderById(Long rentalId);
    void deleteByRentalId(Long rentalId);
}
