package com.locadora_rdt_backend.modules.employees.positions.mapper;

import com.locadora_rdt_backend.modules.employees.positions.dto.PositionDTO;
import com.locadora_rdt_backend.modules.employees.positions.dto.PositionDetailsDTO;
import com.locadora_rdt_backend.modules.employees.positions.dto.PositionInsertDTO;
import com.locadora_rdt_backend.modules.employees.positions.dto.PositionUpdateDTO;
import com.locadora_rdt_backend.modules.employees.positions.model.Position;
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

    public void updateEntity(Position entity, PositionUpdateDTO dto) {
        entity.setName(dto.getName());
    }
}