package com.locadora_rdt_backend.modules.settings.systemsettings.service;

import com.locadora_rdt_backend.modules.settings.systemsettings.dto.SystemSettingDTO;
import com.locadora_rdt_backend.modules.settings.systemsettings.model.SystemSetting;

public interface SystemSettingService {
    SystemSettingDTO findCurrent();
    SystemSettingDTO update(SystemSettingDTO dto);
    SystemSetting findCurrentEntity();
}
