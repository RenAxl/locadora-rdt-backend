package com.locadora_rdt_backend.modules.payment.frequencies.mapper;

import com.locadora_rdt_backend.modules.payment.frequencies.dto.PaymentFrequencyDTO;
import com.locadora_rdt_backend.modules.payment.frequencies.dto.PaymentFrequencyDetailsDTO;
import com.locadora_rdt_backend.modules.payment.frequencies.dto.PaymentFrequencyInsertDTO;
import com.locadora_rdt_backend.modules.payment.frequencies.dto.PaymentFrequencyUpdateDTO;
import com.locadora_rdt_backend.modules.payment.frequencies.model.PaymentFrequency;
import org.springframework.stereotype.Component;

@Component
public class PaymentFrequencyMapper {

    public PaymentFrequencyDTO toDTO(PaymentFrequency entity) {
        if (entity == null) {
            return null;
        }

        return new PaymentFrequencyDTO(
                entity.getId(),
                entity.getFrequency(),
                entity.getDays()
        );
    }

    public PaymentFrequencyDetailsDTO toDetailsDTO(PaymentFrequency entity) {
        if (entity == null) {
            return null;
        }

        PaymentFrequencyDetailsDTO dto = new PaymentFrequencyDetailsDTO();
        dto.setId(entity.getId());
        dto.setFrequency(entity.getFrequency());
        dto.setDays(entity.getDays());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUpdatedBy(entity.getUpdatedBy());

        return dto;
    }

    public PaymentFrequency toEntity(PaymentFrequencyInsertDTO dto) {
        if (dto == null) {
            return null;
        }

        PaymentFrequency entity = new PaymentFrequency();
        entity.setFrequency(dto.getFrequency());
        entity.setDays(dto.getDays());

        return entity;
    }

    public void updateEntity(PaymentFrequency entity, PaymentFrequencyUpdateDTO dto) {
        if (entity == null || dto == null) {
            return;
        }

        entity.setFrequency(dto.getFrequency());
        entity.setDays(dto.getDays());
    }
}
