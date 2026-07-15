package com.locadora_rdt_backend.modules.rental.service;

import com.locadora_rdt_backend.modules.rental.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.time.Instant;
import com.locadora_rdt_backend.modules.customers.dto.CustomerDTO;

public interface RentalService {
    Page<RentalDTO> findAllPaged(String number, String customer, String status, Long rentalTypeId,
                                  Instant dateFrom, Instant dateTo, PageRequest pageRequest);
    RentalDetailsDTO findById(Long id);
    CustomerDTO findCurrentCustomer();
    RentalDTO insert(RentalSaveDTO dto);
    RentalDTO update(Long id, RentalSaveDTO dto);
    RentalDTO confirm(Long id);
    void delete(Long id);
}
