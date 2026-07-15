package com.locadora_rdt_backend.modules.suppliers.mapper;

import com.locadora_rdt_backend.modules.suppliers.dto.*;
import com.locadora_rdt_backend.modules.suppliers.model.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {

    public SupplierDTO toDTO(Supplier entity) {
        if (entity == null) {
            return null;
        }

        SupplierDTO dto = new SupplierDTO();
        copyCommonFields(entity, dto);
        return dto;
    }

    public SupplierDetailsDTO toDetailsDTO(Supplier entity) {
        if (entity == null) {
            return null;
        }

        SupplierDetailsDTO dto = new SupplierDetailsDTO();
        copyCommonFields(entity, dto);
        dto.setVersion(entity.getVersion());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUpdatedBy(entity.getUpdatedBy());
        return dto;
    }

    public Supplier toEntity(SupplierInsertDTO dto) {
        if (dto == null) {
            return null;
        }

        Supplier entity = new Supplier();
        copyToEntity(dto, entity);
        return entity;
    }

    public void copyToEntity(SupplierInsertDTO dto, Supplier entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setName(dto.getName().trim());
        entity.setTradeName(dto.getTradeName().trim());
        entity.setCompanyName(dto.getCompanyName().trim());
        entity.setCnpj(dto.getCnpj().trim());
        entity.setAddress(dto.getAddress());
        entity.setEmail(dto.getEmail().trim());
        entity.setPhoneNumber(dto.getPhoneNumber().trim());
    }

    private void copyCommonFields(Supplier entity, SupplierDTO dto) {
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setTradeName(entity.getTradeName());
        dto.setCompanyName(entity.getCompanyName());
        dto.setCnpj(entity.getCnpj());
        dto.setAddress(entity.getAddress());
        dto.setEmail(entity.getEmail());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setImageContentType(entity.getImageContentType());
    }
}
