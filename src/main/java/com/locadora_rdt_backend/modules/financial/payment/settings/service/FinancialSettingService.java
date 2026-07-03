package com.locadora_rdt_backend.modules.financial.payment.settings.service;

import com.locadora_rdt_backend.modules.financial.payment.settings.dto.FinancialSettingDTO;
import com.locadora_rdt_backend.modules.financial.payment.settings.dto.FinancialSettingUpdateDTO;
import com.locadora_rdt_backend.modules.financial.payment.settings.model.FinancialSetting;

public interface FinancialSettingService {

    FinancialSettingDTO findCurrent();

    FinancialSettingDTO update(FinancialSettingUpdateDTO dto);

    FinancialSetting findCurrentEntity();
}
