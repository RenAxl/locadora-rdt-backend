package com.locadora_rdt_backend.modules.settings.financialsettings.service;

import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.settings.financialsettings.dto.FinancialSettingDTO;
import com.locadora_rdt_backend.modules.settings.financialsettings.dto.FinancialSettingUpdateDTO;
import com.locadora_rdt_backend.modules.settings.financialsettings.mapper.FinancialSettingMapper;
import com.locadora_rdt_backend.modules.settings.financialsettings.model.FinancialSetting;
import com.locadora_rdt_backend.modules.settings.financialsettings.repository.FinancialSettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FinancialSettingServiceImpl implements FinancialSettingService {

    private final FinancialSettingRepository repository;
    private final FinancialSettingMapper mapper;
    private final AuthenticationFacade authenticationFacade;

    public FinancialSettingServiceImpl(
            FinancialSettingRepository repository,
            FinancialSettingMapper mapper,
            AuthenticationFacade authenticationFacade
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.authenticationFacade = authenticationFacade;
    }

    @Override
    @Transactional
    public FinancialSettingDTO findCurrent() {
        return mapper.toDTO(getOrCreateDefault());
    }

    @Override
    @Transactional
    public FinancialSettingDTO update(FinancialSettingUpdateDTO dto) {
        FinancialSetting entity = getOrCreateDefault();

        mapper.updateEntity(entity, dto);
        entity.setUpdatedBy(authenticationFacade.getAuthenticatedUsername());
        entity = repository.save(entity);

        return mapper.toDTO(entity);
    }

    @Override
    @Transactional
    public FinancialSetting findCurrentEntity() {
        return getOrCreateDefault();
    }

    private FinancialSetting getOrCreateDefault() {
        return repository.findBySingletonKey(FinancialSetting.DEFAULT_SINGLETON_KEY)
                .orElseGet(this::createDefault);
    }

    private FinancialSetting createDefault() {
        FinancialSetting entity = new FinancialSetting();
        entity.setSingletonKey(FinancialSetting.DEFAULT_SINGLETON_KEY);
        entity.setCreatedBy(currentUsernameOrSystem());

        return repository.save(entity);
    }

    private String currentUsernameOrSystem() {
        String username = authenticationFacade.getAuthenticatedUsername();
        return username == null || username.trim().isEmpty() ? "SYSTEM" : username;
    }
}
