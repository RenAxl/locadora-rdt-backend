package com.locadora_rdt_backend.modules.rentaltypes.mapper;

import com.locadora_rdt_backend.modules.rentaltypes.model.RentalType;
import com.locadora_rdt_backend.modules.rentaltypes.dto.RentalTypeDTO;
import com.locadora_rdt_backend.modules.rentaltypes.dto.RentalTypeDetailsDTO;
import com.locadora_rdt_backend.modules.rentaltypes.dto.RentalTypeInsertDTO;
import com.locadora_rdt_backend.modules.rentaltypes.dto.RentalTypeUpdateDTO;
import org.springframework.stereotype.Component;

@Component
public class RentalTypeMapper {

    public RentalTypeDTO toDTO(RentalType entity) {
        if (entity == null) {
            return null;
        }

        return new RentalTypeDTO(
                entity.getId(),
                entity.getName(),
                entity.getType(),
                entity.getDays(),
                entity.getActive()
        );
    }

    public RentalTypeDetailsDTO toDetailsDTO(RentalType entity) {
        if (entity == null) {
            return null;
        }

        RentalTypeDetailsDTO dto = new RentalTypeDetailsDTO();
        dto.setId(entity.getId());
        dto.setVersion(entity.getVersion());
        dto.setName(entity.getName());
        dto.setType(entity.getType());
        dto.setDays(entity.getDays());
        dto.setActive(entity.getActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUpdatedBy(entity.getUpdatedBy());

        return dto;
    }

    public RentalType toEntity(RentalTypeInsertDTO dto) {
        if (dto == null) {
            return null;
        }

        RentalType entity = new RentalType();
        entity.setName(dto.getName());
        entity.setType(dto.getType());
        entity.setDays(dto.getDays());
        entity.setActive(true);

        return entity;
    }

    public void copyToEntity(RentalTypeUpdateDTO dto, RentalType entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setName(dto.getName());
        entity.setType(dto.getType());
        entity.setDays(dto.getDays());
    }
}
