package com.locadora_rdt_backend.modules.inventory.stockbalances.service;

import com.locadora_rdt_backend.modules.inventory.stockbalances.dto.StockBalanceDTO;
import com.locadora_rdt_backend.modules.inventory.stockbalances.dto.StockBalanceDetailsDTO;
import com.locadora_rdt_backend.modules.inventory.stockbalances.dto.StockBalanceUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface StockBalanceService {

    Page<StockBalanceDTO> findAllPaged(String name, PageRequest pageRequest);

    StockBalanceDetailsDTO findById(Long id);

    StockBalanceDetailsDTO findByItemId(Long itemId);

    StockBalanceDTO updateMinimum(Long id, Integer minimumQuantity);

    StockBalanceDTO update(Long id, StockBalanceUpdateDTO dto);
}
