package com.locadora_rdt_backend.modules.rentals.rentalhistory.service;

import com.locadora_rdt_backend.modules.rentals.rentalhistory.dto.RentalHistoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RentalHistoryService {
    Page<RentalHistoryDTO> findCurrentCustomerHistory(Pageable pageable);
}
