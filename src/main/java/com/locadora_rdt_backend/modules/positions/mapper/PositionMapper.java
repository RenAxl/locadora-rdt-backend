package com.locadora_rdt_backend.modules.positions.mapper;

import com.locadora_rdt_backend.modules.positions.dto.PositionDTO;
import com.locadora_rdt_backend.modules.positions.dto.PositionDetailsDTO;
import com.locadora_rdt_backend.modules.positions.dto.PositionInsertDTO;
import com.locadora_rdt_backend.modules.positions.dto.PositionUpdateDTO;
import com.locadora_rdt_backend.modules.positions.model.Position;
import org.springframework.stereotype.Component;

@Component
public class PositionMapper {

    public PositionDTO toDTO(Position entity) {
        if (entity == null) {
            return null;
        }

        return new PositionDTO(
                entity.getId(),
                entity.getName()
        );
    }

    public PositionDetailsDTO toDetailsDTO(Position entity) {
        if (entity == null) {
            return null;
        }

        PositionDetailsDTO dto = new PositionDetailsDTO();
        dto.setId(entity.getId());
        dto.setVersion(entity.getVersion());
        dto.setName(entity.getName());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUpdatedBy(entity.getUpdatedBy());

        return dto;
    }

    public Position toEntity(PositionInsertDTO dto) {
        if (dto == null) {
            return null;
        }

        Position entity = new Position();
        entity.setName(dto.getName());

        return entity;
    }

    public void copyToEntity(PositionUpdateDTO dto, Position entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setName(dto.getName());
    }
}