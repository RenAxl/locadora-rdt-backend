package com.locadora_rdt_backend.modules.settings.financialsettings.service;

import com.locadora_rdt_backend.modules.settings.financialsettings.dto.FinancialSettingDTO;
import com.locadora_rdt_backend.modules.settings.financialsettings.dto.FinancialSettingUpdateDTO;
import com.locadora_rdt_backend.modules.settings.financialsettings.model.FinancialSetting;

public interface FinancialSettingService {

    FinancialSettingDTO findCurrent();

    FinancialSettingDTO update(FinancialSettingUpdateDTO dto);

    FinancialSetting findCurrentEntity();
}
