package com.locadora_rdt_backend.modules.payment.methods.mapper;

import com.locadora_rdt_backend.modules.payment.methods.dto.PaymentMethodDTO;
import com.locadora_rdt_backend.modules.payment.methods.dto.PaymentMethodDetailsDTO;
import com.locadora_rdt_backend.modules.payment.methods.dto.PaymentMethodInsertDTO;
import com.locadora_rdt_backend.modules.payment.methods.dto.PaymentMethodUpdateDTO;
import com.locadora_rdt_backend.modules.payment.methods.model.PaymentMethod;
import org.springframework.stereotype.Component;

@Component
public class PaymentMethodMapper {

    public PaymentMethodDTO toDTO(PaymentMethod entity) {
        if (entity == null) {
            return null;
        }

        return new PaymentMethodDTO(
                entity.getId(),
                entity.getName(),
                entity.getFee()
        );
    }

    public PaymentMethodDetailsDTO toDetailsDTO(PaymentMethod entity) {
        if (entity == null) {
            return null;
        }

        PaymentMethodDetailsDTO dto = new PaymentMethodDetailsDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setFee(entity.getFee());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUpdatedBy(entity.getUpdatedBy());

        return dto;
    }

    public PaymentMethod toEntity(PaymentMethodInsertDTO dto) {
        if (dto == null) {
            return null;
        }

        PaymentMethod entity = new PaymentMethod();
        entity.setName(dto.getName());
        entity.setFee(dto.getFee());

        return entity;
    }

    public void updateEntity(PaymentMethod entity, PaymentMethodUpdateDTO dto) {
        if (entity == null || dto == null) {
            return;
        }

        entity.setName(dto.getName());
        entity.setFee(dto.getFee());
    }
}
