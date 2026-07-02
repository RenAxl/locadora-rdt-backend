package com.locadora_rdt_backend.tests.modules.payment.methods.service;

import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.financial.payment.methods.dto.PaymentMethodDTO;
import com.locadora_rdt_backend.modules.financial.payment.methods.dto.PaymentMethodDetailsDTO;
import com.locadora_rdt_backend.modules.financial.payment.methods.dto.PaymentMethodInsertDTO;
import com.locadora_rdt_backend.modules.financial.payment.methods.dto.PaymentMethodUpdateDTO;
import com.locadora_rdt_backend.modules.financial.payment.methods.mapper.PaymentMethodMapper;
import com.locadora_rdt_backend.modules.financial.payment.methods.model.PaymentMethod;
import com.locadora_rdt_backend.modules.financial.payment.methods.repository.PaymentMethodRepository;
import com.locadora_rdt_backend.modules.financial.payment.methods.service.PaymentMethodServiceImpl;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PaymentMethodServiceTests {

    @InjectMocks
    private PaymentMethodServiceImpl service;

    @Mock
    private PaymentMethodRepository repository;

    @Mock
    private PaymentMethodMapper mapper;

    @Mock
    private AuthenticationFacade authenticationFacade;

    private Long existingId;
    private Long nonExistingId;
    private PaymentMethod entity;
    private PaymentMethodDTO dto;
    private PaymentMethodDetailsDTO detailsDTO;
    private PaymentMethodInsertDTO insertDTO;
    private PaymentMethodUpdateDTO updateDTO;

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
    void findAllPagedShouldReturnPageAndNormalizeName() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        PageImpl<PaymentMethod> page = new PageImpl<>(List.of(entity));

        Mockito.when(repository.find("Pix", pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(entity)).thenReturn(dto);

        Page<PaymentMethodDTO> result = service.findAllPaged(" Pix ", pageRequest);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(existingId, result.getContent().get(0).getId());
        Mockito.verify(repository).find("Pix", pageRequest);
    }

    @Test
    void findAllPagedShouldUseEmptyNameWhenNameIsNull() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.find("", pageRequest)).thenReturn(new PageImpl<>(List.of()));

        Page<PaymentMethodDTO> result = service.findAllPaged(null, pageRequest);

        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(repository).find("", pageRequest);
    }

    @Test
    void findByIdShouldReturnDetailsDTOWhenIdExists() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(entity));
        Mockito.when(mapper.toDetailsDTO(entity)).thenReturn(detailsDTO);

        PaymentMethodDetailsDTO result = service.findById(existingId);

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

        PaymentMethod result = service.findEntityById(existingId);

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

        PaymentMethodDTO result = service.insert(insertDTO);

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

        PaymentMethodDTO result = service.update(existingId, updateDTO);

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

    private PaymentMethod createEntity() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(existingId);
        paymentMethod.setName("Pix");
        paymentMethod.setFee(new BigDecimal("0.00"));
        paymentMethod.setCreatedAt(Instant.parse("2026-01-01T10:00:00Z"));
        paymentMethod.setCreatedBy("SYSTEM");
        return paymentMethod;
    }

    private PaymentMethodDTO createDTO() {
        return new PaymentMethodDTO(existingId, "Pix", new BigDecimal("0.00"));
    }

    private PaymentMethodDetailsDTO createDetailsDTO() {
        PaymentMethodDetailsDTO paymentMethodDetailsDTO = new PaymentMethodDetailsDTO();
        paymentMethodDetailsDTO.setId(existingId);
        paymentMethodDetailsDTO.setName("Pix");
        paymentMethodDetailsDTO.setFee(new BigDecimal("0.00"));
        paymentMethodDetailsDTO.setCreatedAt(Instant.parse("2026-01-01T10:00:00Z"));
        paymentMethodDetailsDTO.setCreatedBy("SYSTEM");
        return paymentMethodDetailsDTO;
    }

    private PaymentMethodInsertDTO createInsertDTO() {
        PaymentMethodInsertDTO paymentMethodInsertDTO = new PaymentMethodInsertDTO();
        paymentMethodInsertDTO.setName("Pix");
        paymentMethodInsertDTO.setFee(new BigDecimal("0.00"));
        return paymentMethodInsertDTO;
    }

    private PaymentMethodUpdateDTO createUpdateDTO() {
        PaymentMethodUpdateDTO paymentMethodUpdateDTO = new PaymentMethodUpdateDTO();
        paymentMethodUpdateDTO.setId(existingId);
        paymentMethodUpdateDTO.setName("Cartao de credito");
        paymentMethodUpdateDTO.setFee(new BigDecimal("2.50"));
        return paymentMethodUpdateDTO;
    }
}
