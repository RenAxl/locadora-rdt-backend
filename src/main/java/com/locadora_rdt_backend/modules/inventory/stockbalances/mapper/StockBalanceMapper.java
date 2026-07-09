package com.locadora_rdt_backend.modules.inventory.stockbalances.mapper;

import com.locadora_rdt_backend.modules.inventory.stockbalances.dto.StockBalanceDTO;
import com.locadora_rdt_backend.modules.inventory.stockbalances.dto.StockBalanceDetailsDTO;
import com.locadora_rdt_backend.modules.inventory.stockbalances.dto.StockBalanceUpdateDTO;
import com.locadora_rdt_backend.modules.inventory.stockbalances.model.StockBalance;
import org.springframework.stereotype.Component;

@Component
public class StockBalanceMapper {

    public StockBalanceDTO toDTO(StockBalance entity) {
        if (entity == null) {
            return null;
        }

        StockBalanceDTO dto = new StockBalanceDTO();
        copyBasicData(entity, dto);

        return dto;
    }

    public StockBalanceDetailsDTO toDetailsDTO(StockBalance entity) {
        if (entity == null) {
            return null;
        }

        StockBalanceDetailsDTO dto = new StockBalanceDetailsDTO();
        copyBasicData(entity, dto);
        dto.setVersion(entity.getVersion());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUpdatedBy(entity.getUpdatedBy());

        return dto;
    }

    public void copyToEntity(StockBalanceUpdateDTO dto, StockBalance entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setTotalQuantity(dto.getTotalQuantity());
        entity.setReservedQuantity(dto.getReservedQuantity());
        entity.setUnavailableQuantity(dto.getUnavailableQuantity());
        entity.setMinimumQuantity(dto.getMinimumQuantity());
    }

    private void copyBasicData(StockBalance entity, StockBalanceDTO dto) {
        Integer total = zeroIfNull(entity.getTotalQuantity());
        Integer reserved = zeroIfNull(entity.getReservedQuantity());
        Integer unavailable = zeroIfNull(entity.getUnavailableQuantity());
        Integer minimum = zeroIfNull(entity.getMinimumQuantity());
        Integer available = total - reserved - unavailable;

        dto.setId(entity.getId());
        dto.setItemId(entity.getItem() == null ? null : entity.getItem().getId());
        dto.setItemName(entity.getItem() == null ? null : entity.getItem().getName());
        dto.setTotalQuantity(total);
        dto.setReservedQuantity(reserved);
        dto.setUnavailableQuantity(unavailable);
        dto.setAvailableQuantity(available);
        dto.setMinimumQuantity(minimum);
        dto.setLowStock(available <= minimum);
    }

    private Integer zeroIfNull(Integer value) {
        return value == null ? 0 : value;
    }
}
