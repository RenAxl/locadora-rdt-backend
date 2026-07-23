package com.locadora_rdt_backend.modules.rentals.catalog.service;

import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.stocks.items.constants.ItemErrorMessages;
import com.locadora_rdt_backend.modules.stocks.items.dto.ItemDTO;
import com.locadora_rdt_backend.modules.stocks.items.dto.ItemDetailsDTO;
import com.locadora_rdt_backend.modules.stocks.items.mapper.ItemMapper;
import com.locadora_rdt_backend.modules.stocks.items.model.Item;
import com.locadora_rdt_backend.modules.stocks.items.repository.ItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CatalogServiceImpl implements CatalogService {

    private final ItemRepository repository;
    private final ItemMapper mapper;

    public CatalogServiceImpl(
            ItemRepository repository,
            ItemMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ItemDTO> findAllPaged(String name, Long categoryId, PageRequest pageRequest) {
        return repository.findForCatalog(normalizeName(name), normalizeCategoryId(categoryId), pageRequest)
                .map(mapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDetailsDTO findById(Long id) {
        Item entity = findEntityById(id);

        return mapper.toDetailsDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Item findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ItemErrorMessages.ITEM_NOT_FOUND
                ));
    }

    private String normalizeName(String name) {
        return name == null ? "" : name.trim();
    }

    private Long normalizeCategoryId(Long categoryId) {
        if (categoryId == null || categoryId <= 0) {
            return -1L;
        }

        return categoryId;
    }
}
