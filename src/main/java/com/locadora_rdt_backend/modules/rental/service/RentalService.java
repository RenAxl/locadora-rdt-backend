package com.locadora_rdt_backend.modules.rental.service;

import com.locadora_rdt_backend.modules.rental.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.time.Instant;
import com.locadora_rdt_backend.modules.customers.dto.CustomerDTO;
import java.util.List;

public interface RentalService {
    Page<RentalDTO> findAllPaged(String number, String customer, String status, Long rentalTypeId,
                                  Instant dateFrom, Instant dateTo, PageRequest pageRequest);
    RentalDetailsDTO findById(Long id);
    CustomerDTO findCurrentCustomer();
    RentalDTO insert(RentalSaveDTO dto);
    RentalDTO update(Long id, RentalSaveDTO dto);
    RentalDTO confirm(Long id);
    RentalDTO start(Long id);
    RentalDTO cancel(Long id);
    ItemAvailabilityDTO findAvailability(Long itemId);
    List<ItemUnitDTO> findAvailableUnits(Long itemId);
    List<RentalItemUnitDTO> findRentalUnits(Long rentalId);
    List<RentalStatusHistoryDTO> findHistory(Long rentalId);
    void delete(Long id);
}
