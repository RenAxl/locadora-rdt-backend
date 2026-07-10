package com.locadora_rdt_backend.modules.catalog.service;

import com.locadora_rdt_backend.modules.inventory.items.dto.ItemDTO;
import com.locadora_rdt_backend.modules.inventory.items.dto.ItemDetailsDTO;
import com.locadora_rdt_backend.modules.inventory.items.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface CatalogService {

    Page<ItemDTO> findAllPaged(String name, Long categoryId, PageRequest pageRequest);

    ItemDetailsDTO findById(Long id);

    Item findEntityById(Long id);
}
