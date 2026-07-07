package com.locadora_rdt_backend.tests.modules.financial.payment.settings.service;

import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.financial.payment.settings.dto.FinancialSettingDTO;
import com.locadora_rdt_backend.modules.financial.payment.settings.dto.FinancialSettingUpdateDTO;
import com.locadora_rdt_backend.modules.financial.payment.settings.mapper.FinancialSettingMapper;
import com.locadora_rdt_backend.modules.financial.payment.settings.model.FinancialSetting;
import com.locadora_rdt_backend.modules.financial.payment.settings.repository.FinancialSettingRepository;
import com.locadora_rdt_backend.modules.financial.payment.settings.service.FinancialSettingServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class FinancialSettingServiceTests {

    @InjectMocks
    private FinancialSettingServiceImpl service;

    @Mock
    private FinancialSettingRepository repository;

    @Mock
    private FinancialSettingMapper mapper;

    @Mock
    private AuthenticationFacade authenticationFacade;

    private FinancialSetting entity;
    private FinancialSettingDTO dto;

    @BeforeEach
    void setUp() {
        entity = createEntity();
        dto = new FinancialSettingDTO(1L, new BigDecimal("2.00"), new BigDecimal("1.50"));
    }

    @Test
    void findCurrentShouldReturnExistingSetting() {
        Mockito.when(repository.findBySingletonKey(FinancialSetting.DEFAULT_SINGLETON_KEY))
                .thenReturn(Optional.of(entity));
        Mockito.when(mapper.toDTO(entity)).thenReturn(dto);

        FinancialSettingDTO result = service.findCurrent();

        Assertions.assertEquals(dto, result);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findCurrentShouldCreateDefaultWhenSettingDoesNotExist() {
        Mockito.when(repository.findBySingletonKey(FinancialSetting.DEFAULT_SINGLETON_KEY))
                .thenReturn(Optional.empty());
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin@email.com");
        Mockito.when(repository.save(Mockito.any(FinancialSetting.class))).thenAnswer(invocation -> {
            FinancialSetting saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });
        Mockito.when(mapper.toDTO(Mockito.any(FinancialSetting.class))).thenReturn(dto);

        FinancialSettingDTO result = service.findCurrent();

        Assertions.assertEquals(dto, result);
        Mockito.verify(repository).save(Mockito.argThat(setting ->
                FinancialSetting.DEFAULT_SINGLETON_KEY.equals(setting.getSingletonKey())
                        && "admin@email.com".equals(setting.getCreatedBy())
        ));
    }

    @Test
    void findCurrentShouldCreateDefaultWithSystemWhenUsernameIsBlank() {
        Mockito.when(repository.findBySingletonKey(FinancialSetting.DEFAULT_SINGLETON_KEY))
                .thenReturn(Optional.empty());
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn(" ");
        Mockito.when(repository.save(Mockito.any(FinancialSetting.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(mapper.toDTO(Mockito.any(FinancialSetting.class))).thenReturn(dto);

        service.findCurrent();

        Mockito.verify(repository).save(Mockito.argThat(setting -> "SYSTEM".equals(setting.getCreatedBy())));
    }

    @Test
    void updateShouldUpdateExistingSettingAndSetUpdatedBy() {
        FinancialSettingUpdateDTO updateDTO = createUpdateDTO();
        Mockito.when(repository.findBySingletonKey(FinancialSetting.DEFAULT_SINGLETON_KEY))
                .thenReturn(Optional.of(entity));
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin@email.com");
        Mockito.when(repository.save(entity)).thenReturn(entity);
        Mockito.when(mapper.toDTO(entity)).thenReturn(dto);

        FinancialSettingDTO result = service.update(updateDTO);

        Assertions.assertEquals(dto, result);
        Assertions.assertEquals("admin@email.com", entity.getUpdatedBy());
        Mockito.verify(mapper).updateEntity(entity, updateDTO);
        Mockito.verify(repository).save(entity);
    }

    @Test
    void updateShouldCreateDefaultBeforeUpdatingWhenSettingDoesNotExist() {
        FinancialSettingUpdateDTO updateDTO = createUpdateDTO();
        Mockito.when(repository.findBySingletonKey(FinancialSetting.DEFAULT_SINGLETON_KEY))
                .thenReturn(Optional.empty());
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin@email.com");
        Mockito.when(repository.save(Mockito.any(FinancialSetting.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(mapper.toDTO(Mockito.any(FinancialSetting.class))).thenReturn(dto);

        FinancialSettingDTO result = service.update(updateDTO);

        Assertions.assertEquals(dto, result);
        Mockito.verify(repository, Mockito.times(2)).save(Mockito.any(FinancialSetting.class));
        Mockito.verify(mapper).updateEntity(Mockito.any(FinancialSetting.class), Mockito.eq(updateDTO));
    }

    @Test
    void findCurrentEntityShouldReturnExistingEntity() {
        Mockito.when(repository.findBySingletonKey(FinancialSetting.DEFAULT_SINGLETON_KEY))
                .thenReturn(Optional.of(entity));

        FinancialSetting result = service.findCurrentEntity();

        Assertions.assertSame(entity, result);
    }

    private FinancialSetting createEntity() {
        FinancialSetting setting = new FinancialSetting();
        setting.setId(1L);
        setting.setSingletonKey(FinancialSetting.DEFAULT_SINGLETON_KEY);
        setting.setDefaultLateFeePercent(new BigDecimal("2.00"));
        setting.setDefaultLateInterestPercent(new BigDecimal("1.50"));
        setting.setCreatedBy("SYSTEM");
        return setting;
    }

    private FinancialSettingUpdateDTO createUpdateDTO() {
        FinancialSettingUpdateDTO updateDTO = new FinancialSettingUpdateDTO();
        updateDTO.setDefaultLateFeePercent(new BigDecimal("2.00"));
        updateDTO.setDefaultLateInterestPercent(new BigDecimal("1.50"));
        return updateDTO;
    }
}
