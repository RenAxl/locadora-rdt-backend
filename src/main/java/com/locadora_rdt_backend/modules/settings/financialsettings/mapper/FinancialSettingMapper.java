package com.locadora_rdt_backend.modules.settings.financialsettings.mapper;

import com.locadora_rdt_backend.modules.settings.financialsettings.dto.FinancialSettingDTO;
import com.locadora_rdt_backend.modules.settings.financialsettings.dto.FinancialSettingDetailsDTO;
import com.locadora_rdt_backend.modules.settings.financialsettings.dto.FinancialSettingUpdateDTO;
import com.locadora_rdt_backend.modules.settings.financialsettings.model.FinancialSetting;
import org.springframework.stereotype.Component;

@Component
public class FinancialSettingMapper {

    public FinancialSettingDTO toDTO(FinancialSetting entity) {
        if (entity == null) {
            return null;
        }

        return new FinancialSettingDTO(
                entity.getId(),
                entity.getDefaultLateFeePercent(),
                entity.getDefaultLateInterestPercent()
        );
    }

    public FinancialSettingDetailsDTO toDetailsDTO(FinancialSetting entity) {
        if (entity == null) {
            return null;
        }

        FinancialSettingDetailsDTO dto = new FinancialSettingDetailsDTO(
                entity.getId(),
                entity.getDefaultLateFeePercent(),
                entity.getDefaultLateInterestPercent()
        );
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUpdatedBy(entity.getUpdatedBy());

        return dto;
    }

    public void updateEntity(FinancialSetting entity, FinancialSettingUpdateDTO dto) {
        if (entity == null || dto == null) {
            return;
        }

        entity.setDefaultLateFeePercent(dto.getDefaultLateFeePercent());
        entity.setDefaultLateInterestPercent(dto.getDefaultLateInterestPercent());
    }
}
