package com.locadora_rdt_backend.modules.rentals.catalog.service;

import com.locadora_rdt_backend.modules.stocks.items.dto.ItemDTO;
import com.locadora_rdt_backend.modules.stocks.items.dto.ItemDetailsDTO;
import com.locadora_rdt_backend.modules.stocks.items.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface CatalogService {

    Page<ItemDTO> findAllPaged(String name, Long categoryId, PageRequest pageRequest);

    ItemDetailsDTO findById(Long id);

    Item findEntityById(Long id);
}
