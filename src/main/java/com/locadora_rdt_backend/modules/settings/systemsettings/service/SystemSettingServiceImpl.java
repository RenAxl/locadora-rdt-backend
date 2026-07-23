package com.locadora_rdt_backend.modules.settings.systemsettings.service;

import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.settings.systemsettings.dto.SystemSettingDTO;
import com.locadora_rdt_backend.modules.settings.systemsettings.model.SystemSetting;
import com.locadora_rdt_backend.modules.settings.systemsettings.repository.SystemSettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SystemSettingServiceImpl implements SystemSettingService {
    private final SystemSettingRepository repository;
    private final AuthenticationFacade authenticationFacade;

    public SystemSettingServiceImpl(SystemSettingRepository repository, AuthenticationFacade authenticationFacade) {
        this.repository = repository;
        this.authenticationFacade = authenticationFacade;
    }

    @Override
    @Transactional
    public SystemSettingDTO findCurrent() {
        return toDTO(getOrCreateDefault());
    }

    @Override
    @Transactional
    public SystemSettingDTO update(SystemSettingDTO dto) {
        SystemSetting entity = getOrCreateDefault();
        entity.setCompanyName(dto.getCompanyName());
        entity.setAddress(dto.getAddress());
        entity.setUpdatedBy(authenticationFacade.getAuthenticatedUsername());
        return toDTO(repository.save(entity));
    }

    @Override
    @Transactional
    public SystemSetting findCurrentEntity() {
        return getOrCreateDefault();
    }

    private SystemSetting getOrCreateDefault() {
        return repository.findBySingletonKey(SystemSetting.DEFAULT_SINGLETON_KEY)
                .orElseGet(this::createDefault);
    }

    private SystemSetting createDefault() {
        SystemSetting entity = new SystemSetting();
        entity.setSingletonKey(SystemSetting.DEFAULT_SINGLETON_KEY);
        entity.setCompanyName("Locadora RDT");
        entity.setCreatedBy(currentUsernameOrSystem());
        return repository.save(entity);
    }

    private SystemSettingDTO toDTO(SystemSetting entity) {
        return new SystemSettingDTO(entity.getId(), entity.getCompanyName(), entity.getAddress());
    }

    private String currentUsernameOrSystem() {
        String username = authenticationFacade.getAuthenticatedUsername();
        return username == null || username.trim().isEmpty() ? "SYSTEM" : username;
    }
}
