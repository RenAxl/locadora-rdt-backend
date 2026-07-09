package com.locadora_rdt_backend.modules.inventory.items.mapper;

import com.locadora_rdt_backend.modules.rental.categories.dto.CategoryDTO;
import com.locadora_rdt_backend.modules.inventory.items.dto.ItemDTO;
import com.locadora_rdt_backend.modules.inventory.items.dto.ItemDetailsDTO;
import com.locadora_rdt_backend.modules.inventory.items.dto.ItemInsertDTO;
import com.locadora_rdt_backend.modules.inventory.items.dto.ItemUpdateDTO;
import com.locadora_rdt_backend.modules.inventory.items.model.Item;
import com.locadora_rdt_backend.modules.rental.rentaltypes.dto.RentalTypeDTO;
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
        dto.setPrice(entity.getPrice());
        dto.setQuantity(entity.getQuantity());
        dto.setRentedQuantity(entity.getRentedQuantity());
        dto.setActive(entity.getActive());
        dto.setCategory(toCategoryDTO(entity));
        dto.setRentalType(toRentalTypeDTO(entity));

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
        dto.setPrice(entity.getPrice());
        dto.setQuantity(entity.getQuantity());
        dto.setRentedQuantity(entity.getRentedQuantity());
        dto.setActive(entity.getActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setCategory(toCategoryDTO(entity));
        dto.setRentalType(toRentalTypeDTO(entity));

        return dto;
    }

    public Item toEntity(ItemInsertDTO dto) {
        if (dto == null) {
            return null;
        }

        Item entity = new Item();
        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        entity.setQuantity(dto.getQuantity());
        entity.setActive(true);
        entity.setRentedQuantity(0);

        return entity;
    }

    public void copyToEntity(ItemUpdateDTO dto, Item entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        entity.setQuantity(dto.getQuantity());
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

    private RentalTypeDTO toRentalTypeDTO(Item entity) {
        if (entity.getRentalType() == null) {
            return null;
        }

        return new RentalTypeDTO(
                entity.getRentalType().getId(),
                entity.getRentalType().getName(),
                entity.getRentalType().getType(),
                entity.getRentalType().getActive()
        );
    }
}
