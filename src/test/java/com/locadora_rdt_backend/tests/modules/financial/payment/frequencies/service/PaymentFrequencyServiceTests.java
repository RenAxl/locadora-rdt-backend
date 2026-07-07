package com.locadora_rdt_backend.tests.modules.financial.payment.frequencies.service;

import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.financial.payment.frequencies.dto.PaymentFrequencyDTO;
import com.locadora_rdt_backend.modules.financial.payment.frequencies.dto.PaymentFrequencyDetailsDTO;
import com.locadora_rdt_backend.modules.financial.payment.frequencies.dto.PaymentFrequencyInsertDTO;
import com.locadora_rdt_backend.modules.financial.payment.frequencies.dto.PaymentFrequencyUpdateDTO;
import com.locadora_rdt_backend.modules.financial.payment.frequencies.mapper.PaymentFrequencyMapper;
import com.locadora_rdt_backend.modules.financial.payment.frequencies.model.PaymentFrequency;
import com.locadora_rdt_backend.modules.financial.payment.frequencies.repository.PaymentFrequencyRepository;
import com.locadora_rdt_backend.modules.financial.payment.frequencies.service.PaymentFrequencyServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PaymentFrequencyServiceTests {

    @InjectMocks
    private PaymentFrequencyServiceImpl service;

    @Mock
    private PaymentFrequencyRepository repository;

    @Mock
    private PaymentFrequencyMapper mapper;

    @Mock
    private AuthenticationFacade authenticationFacade;

    private Long existingId;
    private Long nonExistingId;
    private PaymentFrequency entity;
    private PaymentFrequencyDTO dto;
    private PaymentFrequencyDetailsDTO detailsDTO;
    private PaymentFrequencyInsertDTO insertDTO;
    private PaymentFrequencyUpdateDTO updateDTO;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 999L;

        entity = createEntity();
        dto = createDTO();
        detailsDTO = createDetailsDTO();
        insertDTO = createInsertDTO();
        updateDTO = createUpdateDTO();
    }

    @Test
    void findAllPagedShouldReturnPageAndNormalizeFrequency() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        PageImpl<PaymentFrequency> page = new PageImpl<>(List.of(entity));

        Mockito.when(repository.find("Mensal", pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(entity)).thenReturn(dto);

        Page<PaymentFrequencyDTO> result = service.findAllPaged(" Mensal ", pageRequest);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(existingId, result.getContent().get(0).getId());
        Mockito.verify(repository).find("Mensal", pageRequest);
    }

    @Test
    void findAllPagedShouldUseEmptyFrequencyWhenFrequencyIsNull() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.find("", pageRequest)).thenReturn(new PageImpl<>(List.of()));

        Page<PaymentFrequencyDTO> result = service.findAllPaged(null, pageRequest);

        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(repository).find("", pageRequest);
    }

    @Test
    void findByIdShouldReturnDetailsDTOWhenIdExists() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(entity));
        Mockito.when(mapper.toDetailsDTO(entity)).thenReturn(detailsDTO);

        PaymentFrequencyDetailsDTO result = service.findById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.getId());
    }

    @Test
    void findByIdShouldThrowResourceNotFoundWhenIdDoesNotExist() {
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(nonExistingId));
    }

    @Test
    void findEntityByIdShouldReturnEntityWhenIdExists() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(entity));

        PaymentFrequency result = service.findEntityById(existingId);

        Assertions.assertEquals(entity, result);
    }

    @Test
    void findEntityByIdShouldThrowResourceNotFoundWhenIdDoesNotExist() {
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findEntityById(nonExistingId));
    }

    @Test
    void insertShouldSetCreatedBySaveAndReturnDTO() {
        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(entity);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(entity)).thenReturn(entity);
        Mockito.when(mapper.toDTO(entity)).thenReturn(dto);

        PaymentFrequencyDTO result = service.insert(insertDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("admin", entity.getCreatedBy());
        Mockito.verify(repository).save(entity);
    }

    @Test
    void updateShouldSetUpdatedBySaveAndReturnDTOWhenIdExists() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(entity));
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(entity)).thenReturn(entity);
        Mockito.when(mapper.toDTO(entity)).thenReturn(dto);

        PaymentFrequencyDTO result = service.update(existingId, updateDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("admin", entity.getUpdatedBy());
        Mockito.verify(mapper).updateEntity(entity, updateDTO);
        Mockito.verify(repository).save(entity);
    }

    @Test
    void updateShouldThrowResourceNotFoundWhenIdDoesNotExist() {
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(nonExistingId, updateDTO));
    }

    @Test
    void deleteShouldDeleteAndFlushWhenIdExists() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(entity));

        service.delete(existingId);

        Mockito.verify(repository).delete(entity);
        Mockito.verify(repository).flush();
    }

    @Test
    void deleteShouldThrowResourceNotFoundWhenIdDoesNotExist() {
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistingId));
    }

    @Test
    void deleteShouldThrowDatabaseExceptionWhenIntegrityViolation() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(entity));
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).flush();

        Assertions.assertThrows(DatabaseException.class, () -> service.delete(existingId));
    }

    @Test
    void deleteAllShouldDeleteExistingIdsAndFlush() {
        List<Long> ids = List.of(existingId);

        Mockito.when(repository.findAllById(ids)).thenReturn(List.of(entity));

        service.deleteAll(ids);

        Mockito.verify(repository).deleteAllByIds(ids);
        Mockito.verify(repository).flush();
    }

    @Test
    void deleteAllShouldThrowIllegalArgumentExceptionWhenIdsAreNullOrEmpty() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.deleteAll(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.deleteAll(List.of()));
    }

    @Test
    void deleteAllShouldThrowResourceNotFoundWhenAnyIdDoesNotExist() {
        List<Long> ids = List.of(existingId, nonExistingId);

        Mockito.when(repository.findAllById(ids)).thenReturn(List.of(entity));

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.deleteAll(ids));
        Mockito.verify(repository, Mockito.never()).deleteAllByIds(Mockito.anyList());
    }

    @Test
    void deleteAllShouldThrowDatabaseExceptionWhenIntegrityViolation() {
        List<Long> ids = List.of(existingId);

        Mockito.when(repository.findAllById(ids)).thenReturn(List.of(entity));
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).flush();

        Assertions.assertThrows(DatabaseException.class, () -> service.deleteAll(ids));
    }

    private PaymentFrequency createEntity() {
        PaymentFrequency paymentFrequency = new PaymentFrequency();
        paymentFrequency.setId(existingId);
        paymentFrequency.setFrequency("Mensal");
        paymentFrequency.setDays(30);
        paymentFrequency.setCreatedAt(Instant.parse("2026-01-01T10:00:00Z"));
        paymentFrequency.setCreatedBy("SYSTEM");
        return paymentFrequency;
    }

    private PaymentFrequencyDTO createDTO() {
        return new PaymentFrequencyDTO(existingId, "Mensal", 30);
    }

    private PaymentFrequencyDetailsDTO createDetailsDTO() {
        PaymentFrequencyDetailsDTO paymentFrequencyDetailsDTO = new PaymentFrequencyDetailsDTO();
        paymentFrequencyDetailsDTO.setId(existingId);
        paymentFrequencyDetailsDTO.setFrequency("Mensal");
        paymentFrequencyDetailsDTO.setDays(30);
        paymentFrequencyDetailsDTO.setCreatedAt(Instant.parse("2026-01-01T10:00:00Z"));
        paymentFrequencyDetailsDTO.setCreatedBy("SYSTEM");
        return paymentFrequencyDetailsDTO;
    }

    private PaymentFrequencyInsertDTO createInsertDTO() {
        PaymentFrequencyInsertDTO paymentFrequencyInsertDTO = new PaymentFrequencyInsertDTO();
        paymentFrequencyInsertDTO.setFrequency("Mensal");
        paymentFrequencyInsertDTO.setDays(30);
        return paymentFrequencyInsertDTO;
    }

    private PaymentFrequencyUpdateDTO createUpdateDTO() {
        PaymentFrequencyUpdateDTO paymentFrequencyUpdateDTO = new PaymentFrequencyUpdateDTO();
        paymentFrequencyUpdateDTO.setId(existingId);
        paymentFrequencyUpdateDTO.setFrequency("Quinzenal");
        paymentFrequencyUpdateDTO.setDays(15);
        return paymentFrequencyUpdateDTO;
    }
}
