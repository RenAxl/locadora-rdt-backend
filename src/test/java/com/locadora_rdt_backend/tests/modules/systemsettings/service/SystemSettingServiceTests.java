package com.locadora_rdt_backend.tests.modules.systemsettings.service;

import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.systemsettings.dto.SystemSettingDTO;
import com.locadora_rdt_backend.modules.systemsettings.model.Address;
import com.locadora_rdt_backend.modules.systemsettings.model.SystemSetting;
import com.locadora_rdt_backend.modules.systemsettings.repository.SystemSettingRepository;
import com.locadora_rdt_backend.modules.systemsettings.service.SystemSettingServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class SystemSettingServiceTests {

    @Mock private SystemSettingRepository repository;
    @Mock private AuthenticationFacade authenticationFacade;

    private SystemSettingServiceImpl service;
    private SystemSetting entity;

    @BeforeEach
    void setUp() {
        service = new SystemSettingServiceImpl(repository, authenticationFacade);
        entity = new SystemSetting();
        entity.setId(1L);
        entity.setCompanyName("Locadora RDT");
        entity.setAddress(createAddress("70000-000"));
    }

    @Test
    void findCurrentShouldReturnExistingSetting() {
        Mockito.when(repository.findBySingletonKey(SystemSetting.DEFAULT_SINGLETON_KEY))
                .thenReturn(Optional.of(entity));

        SystemSettingDTO result = service.findCurrent();

        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals("Locadora RDT", result.getCompanyName());
        Assertions.assertEquals("70000-000", result.getAddress().getZipCode());
    }

    @Test
    void findCurrentShouldCreateDefaultSetting() {
        Mockito.when(repository.findBySingletonKey(SystemSetting.DEFAULT_SINGLETON_KEY))
                .thenReturn(Optional.empty());
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin@email.com");
        Mockito.when(repository.save(Mockito.any(SystemSetting.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SystemSettingDTO result = service.findCurrent();

        Assertions.assertEquals("Locadora RDT", result.getCompanyName());
        ArgumentCaptor<SystemSetting> captor = ArgumentCaptor.forClass(SystemSetting.class);
        Mockito.verify(repository).save(captor.capture());
        Assertions.assertEquals("admin@email.com", captor.getValue().getCreatedBy());
    }

    @Test
    void findCurrentShouldUseSystemWhenThereIsNoAuthenticatedUsername() {
        Mockito.when(repository.findBySingletonKey(SystemSetting.DEFAULT_SINGLETON_KEY))
                .thenReturn(Optional.empty());
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn(null);
        Mockito.when(repository.save(Mockito.any(SystemSetting.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.findCurrent();

        ArgumentCaptor<SystemSetting> captor = ArgumentCaptor.forClass(SystemSetting.class);
        Mockito.verify(repository).save(captor.capture());
        Assertions.assertEquals("SYSTEM", captor.getValue().getCreatedBy());
    }

    @Test
    void updateShouldChangeDataAndRegisterUser() {
        SystemSettingDTO dto = new SystemSettingDTO(null, "Nova Locadora", createAddress("01001-001"));
        Mockito.when(repository.findBySingletonKey(SystemSetting.DEFAULT_SINGLETON_KEY))
                .thenReturn(Optional.of(entity));
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin@email.com");
        Mockito.when(repository.save(entity)).thenReturn(entity);

        SystemSettingDTO result = service.update(dto);

        Assertions.assertEquals("Nova Locadora", result.getCompanyName());
        Assertions.assertEquals("01001-001", result.getAddress().getZipCode());
        Assertions.assertEquals("admin@email.com", entity.getUpdatedBy());
    }

    private Address createAddress(String zipCode) {
        Address address = new Address();
        address.setStreet("Rua Teste");
        address.setNumber("10");
        address.setNeighborhood("Centro");
        address.setCity("Brasília");
        address.setState("DF");
        address.setZipCode(zipCode);
        return address;
    }
}
