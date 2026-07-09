package com.locadora_rdt_backend.modules.inventory.stockmovements.service;

import com.locadora_rdt_backend.modules.inventory.stockmovements.dto.StockMovementDTO;
import com.locadora_rdt_backend.modules.inventory.stockmovements.dto.StockMovementInsertDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface StockMovementService {

    Page<StockMovementDTO> findAllPaged(String name, PageRequest pageRequest);

    StockMovementDTO findById(Long id);

    StockMovementDTO insert(StockMovementInsertDTO dto);
}
