package com.locadora_rdt_backend.modules.receivables.mapper;

import com.locadora_rdt_backend.modules.receivables.dto.ReceivableDTO;
import com.locadora_rdt_backend.modules.receivables.dto.ReceivableInsertDTO;
import com.locadora_rdt_backend.modules.receivables.model.Receivable;
import org.springframework.stereotype.Component;

@Component
public class ReceivableMapper {

    public ReceivableDTO toDTO(Receivable entity) {
        ReceivableDTO dto = new ReceivableDTO();

        dto.setId(entity.getId());
        dto.setCustomerId(entity.getCustomerId());
        dto.setDescription(entity.getDescription());
        dto.setAmount(entity.getAmount());
        dto.setDueDate(entity.getDueDate());
        dto.setPaymentDate(entity.getPaymentDate());
        dto.setPaymentMethodId(entity.getPaymentMethodId());
        dto.setFrequencyId(entity.getFrequencyId());
        dto.setNote(entity.getNote());
        dto.setFileName(entity.getFileName());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setCreatedBy(entity.getCreatedBy());

        return dto;
    }

    public Receivable toEntity(ReceivableInsertDTO dto) {
        Receivable entity = new Receivable();

        entity.setCustomerId(normalizeId(dto.getCustomerId()));
        entity.setDescription(trimToNull(dto.getDescription()));
        entity.setAmount(dto.getAmount());
        entity.setDueDate(dto.getDueDate());
        entity.setPaymentDate(dto.getPaymentDate());
        entity.setPaymentMethodId(normalizeId(dto.getPaymentMethodId()));
        entity.setFrequencyId(normalizeId(dto.getFrequencyId()));
        entity.setNote(trimToNull(dto.getNote()));
        entity.setFileName(trimToNull(dto.getFileName()));
        entity.normalizeDefaults();

        return entity;
    }

    private Long normalizeId(Long value) {
        if (value == null || value < 1L) {
            return 0L;
        }
        return value;
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
