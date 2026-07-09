package com.locadora_rdt_backend.modules.inventory.stockmovements.mapper;

import com.locadora_rdt_backend.modules.inventory.stockmovements.dto.StockMovementDTO;
import com.locadora_rdt_backend.modules.inventory.stockmovements.dto.StockMovementInsertDTO;
import com.locadora_rdt_backend.modules.inventory.stockmovements.model.StockMovement;
import org.springframework.stereotype.Component;

@Component
public class StockMovementMapper {

    public StockMovementDTO toDTO(StockMovement entity) {
        if (entity == null) {
            return null;
        }

        StockMovementDTO dto = new StockMovementDTO();
        dto.setId(entity.getId());
        dto.setItemId(entity.getItem() == null ? null : entity.getItem().getId());
        dto.setItemName(entity.getItem() == null ? null : entity.getItem().getName());
        dto.setType(entity.getType());
        dto.setQuantity(entity.getQuantity());
        dto.setReason(entity.getReason());
        dto.setReferenceType(entity.getReferenceType());
        dto.setReferenceId(entity.getReferenceId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setCreatedBy(entity.getCreatedBy());

        return dto;
    }

    public StockMovement toEntity(StockMovementInsertDTO dto) {
        if (dto == null) {
            return null;
        }

        StockMovement entity = new StockMovement();
        entity.setType(dto.getType());
        entity.setQuantity(dto.getQuantity());
        entity.setReason(dto.getReason());
        entity.setReferenceType(dto.getReferenceType());
        entity.setReferenceId(dto.getReferenceId());

        return entity;
    }
}
