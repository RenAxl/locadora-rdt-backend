package com.locadora_rdt_backend.modules.stocks.items.mapper;

import com.locadora_rdt_backend.modules.stocks.categories.dto.CategoryDTO;
import com.locadora_rdt_backend.modules.stocks.items.dto.ItemDTO;
import com.locadora_rdt_backend.modules.stocks.items.dto.ItemDetailsDTO;
import com.locadora_rdt_backend.modules.stocks.items.dto.ItemInsertDTO;
import com.locadora_rdt_backend.modules.stocks.items.dto.ItemUpdateDTO;
import com.locadora_rdt_backend.modules.stocks.items.model.Item;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper {

    public ItemDTO toDTO(Item entity) {
        if (entity == null) {
            return null;
        }

        ItemDTO dto = new ItemDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setActive(entity.getActive());
        dto.setCategory(toCategoryDTO(entity));

        return dto;
    }

    public ItemDetailsDTO toDetailsDTO(Item entity) {
        if (entity == null) {
            return null;
        }

        ItemDetailsDTO dto = new ItemDetailsDTO();
        dto.setId(entity.getId());
        dto.setVersion(entity.getVersion());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setActive(entity.getActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setCategory(toCategoryDTO(entity));

        return dto;
    }

    public Item toEntity(ItemInsertDTO dto) {
        if (dto == null) {
            return null;
        }

        Item entity = new Item();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setActive(true);

        return entity;
    }

    public void copyToEntity(ItemUpdateDTO dto, Item entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
    }

    private CategoryDTO toCategoryDTO(Item entity) {
        if (entity.getCategory() == null) {
            return null;
        }

        return new CategoryDTO(
                entity.getCategory().getId(),
                entity.getCategory().getName(),
                entity.getCategory().getActive()
        );
    }

}
