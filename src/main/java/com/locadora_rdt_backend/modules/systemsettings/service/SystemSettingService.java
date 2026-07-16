package com.locadora_rdt_backend.modules.systemsettings.service;

import com.locadora_rdt_backend.modules.systemsettings.dto.SystemSettingDTO;
import com.locadora_rdt_backend.modules.systemsettings.model.SystemSetting;

public interface SystemSettingService {
    SystemSettingDTO findCurrent();
    SystemSettingDTO update(SystemSettingDTO dto);
    SystemSetting findCurrentEntity();
}
