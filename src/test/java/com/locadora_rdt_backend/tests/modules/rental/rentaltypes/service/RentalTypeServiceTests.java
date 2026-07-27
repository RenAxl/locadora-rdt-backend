package com.locadora_rdt_backend.tests.modules.rental.rentaltypes.service;

import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.rentals.rentaltypes.dto.RentalTypeDTO;
import com.locadora_rdt_backend.modules.rentals.rentaltypes.dto.RentalTypeDetailsDTO;
import com.locadora_rdt_backend.modules.rentals.rentaltypes.dto.RentalTypeInsertDTO;
import com.locadora_rdt_backend.modules.rentals.rentaltypes.dto.RentalTypeUpdateDTO;
import com.locadora_rdt_backend.modules.rentals.rentaltypes.mapper.RentalTypeMapper;
import com.locadora_rdt_backend.modules.rentals.rentaltypes.model.RentalType;
import com.locadora_rdt_backend.modules.rentals.rentaltypes.repository.RentalTypeRepository;
import com.locadora_rdt_backend.modules.rentals.rentaltypes.service.RentalTypeServiceImpl;
import com.locadora_rdt_backend.tests.modules.rental.rentaltypes.factory.RentalTypeFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("java:S5778")
class RentalTypeServiceTests {

    @InjectMocks
    private RentalTypeServiceImpl service;

    @Mock
    private RentalTypeRepository repository;

    @Mock
    private RentalTypeMapper mapper;

    @Mock
    private AuthenticationFacade authenticationFacade;

    private Long existingId;
    private Long nonExistingId;

    private RentalType rentalType;
    private RentalTypeDTO rentalTypeDTO;
    private RentalTypeDetailsDTO rentalTypeDetailsDTO;
    private RentalTypeInsertDTO insertDTO;
    private RentalTypeUpdateDTO updateDTO;

    private PageImpl<RentalType> page;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 999L;

        rentalType = RentalTypeFactory.createRentalType();
        rentalTypeDTO = RentalTypeFactory.createRentalTypeDTO(rentalType);
        rentalTypeDetailsDTO = RentalTypeFactory.createRentalTypeDetailsDTO(rentalType);
        insertDTO = RentalTypeFactory.createRentalTypeInsertDTO();
        updateDTO = RentalTypeFactory.createRentalTypeUpdateDTO();

        page = new PageImpl<>(List.of(rentalType));
    }

    @Test
    void findAllPagedShouldReturnPage() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.find("", pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(rentalType)).thenReturn(rentalTypeDTO);

        Page<RentalTypeDTO> result = service.findAllPaged("", pageRequest);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    void findAllPagedShouldReturnEmptyPageWhenNoData() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        PageImpl<RentalType> emptyPage = new PageImpl<>(List.of());

        Mockito.when(repository.find("", pageRequest)).thenReturn(emptyPage);

        Page<RentalTypeDTO> result = service.findAllPaged("", pageRequest);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void findAllPagedShouldCallRepository() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.find("", pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(rentalType)).thenReturn(rentalTypeDTO);

        service.findAllPaged("", pageRequest);

        Mockito.verify(repository).find("", pageRequest);
    }

    @Test
    void findAllPagedShouldTrimName() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.find("Locação Diária", pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(rentalType)).thenReturn(rentalTypeDTO);

        service.findAllPaged(" Locação Diária ", pageRequest);

        Mockito.verify(repository).find("Locação Diária", pageRequest);
    }

    @Test
    void findAllPagedShouldUseEmptyNameWhenNameIsNull() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.find("", pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(rentalType)).thenReturn(rentalTypeDTO);

        service.findAllPaged(null, pageRequest);

        Mockito.verify(repository).find("", pageRequest);
    }

    @Test
    void findByIdShouldReturnDTOWhenIdExists() {
        Mockito.when(repository.findById(existingId))
                .thenReturn(Optional.of(rentalType));

        Mockito.when(mapper.toDetailsDTO(rentalType))
                .thenReturn(rentalTypeDetailsDTO);

        RentalTypeDetailsDTO result = service.findById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.getId());
    }

    @Test
    void findByIdShouldThrowExceptionWhenIdDoesNotExist() {
        Mockito.when(repository.findById(nonExistingId))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });
    }

    @Test
    void findByIdShouldCallRepository() {
        Mockito.when(repository.findById(existingId))
                .thenReturn(Optional.of(rentalType));

        Mockito.when(mapper.toDetailsDTO(rentalType))
                .thenReturn(rentalTypeDetailsDTO);

        service.findById(existingId);

        Mockito.verify(repository).findById(existingId);
    }

    @Test
    void findEntityByIdShouldReturnEntityWhenIdExists() {
        Mockito.when(repository.findById(existingId))
                .thenReturn(Optional.of(rentalType));

        RentalType result = service.findEntityById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(rentalType, result);
    }

    @Test
    void findEntityByIdShouldThrowExceptionWhenIdDoesNotExist() {
        Mockito.when(repository.findById(nonExistingId))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findEntityById(nonExistingId);
        });
    }

    @Test
    void insertShouldReturnDTO() {
        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(rentalType);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(rentalType)).thenReturn(rentalType);
        Mockito.when(mapper.toDTO(rentalType)).thenReturn(rentalTypeDTO);

        RentalTypeDTO result = service.insert(insertDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(rentalType.getName(), result.getName());
    }

    @Test
    void insertShouldReturnDTOWithType() {
        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(rentalType);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(rentalType)).thenReturn(rentalType);
        Mockito.when(mapper.toDTO(rentalType)).thenReturn(rentalTypeDTO);

        RentalTypeDTO result = service.insert(insertDTO);

        Assertions.assertEquals(rentalType.getType(), result.getType());
    }

    @Test
    void insertShouldReturnDTOWithDays() {
        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(rentalType);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(rentalType)).thenReturn(rentalType);
        Mockito.when(mapper.toDTO(rentalType)).thenReturn(rentalTypeDTO);

        RentalTypeDTO result = service.insert(insertDTO);

        Assertions.assertEquals(rentalType.getDays(), result.getDays());
    }

    @Test
    void insertShouldCallRepositorySave() {
        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(rentalType);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(rentalType)).thenReturn(rentalType);
        Mockito.when(mapper.toDTO(rentalType)).thenReturn(rentalTypeDTO);

        service.insert(insertDTO);

        Mockito.verify(repository).save(rentalType);
    }

    @Test
    void insertShouldMapEntityCorrectly() {
        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(rentalType);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(rentalType)).thenReturn(rentalType);
        Mockito.when(mapper.toDTO(rentalType)).thenReturn(rentalTypeDTO);

        service.insert(insertDTO);

        Mockito.verify(mapper).toEntity(insertDTO);
    }

    @Test
    void insertShouldSetCreatedBy() {
        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(rentalType);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(rentalType)).thenReturn(rentalType);
        Mockito.when(mapper.toDTO(rentalType)).thenReturn(rentalTypeDTO);

        service.insert(insertDTO);

        Assertions.assertEquals("admin", rentalType.getCreatedBy());
    }

    @Test
    void insertShouldSetActiveTrue() {
        rentalType.setActive(false);

        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(rentalType);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(rentalType)).thenReturn(rentalType);
        Mockito.when(mapper.toDTO(rentalType)).thenReturn(rentalTypeDTO);

        service.insert(insertDTO);

        Assertions.assertTrue(rentalType.getActive());
    }

    @Test
    void updateShouldReturnDTOWhenIdExists() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(rentalType));
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(rentalType)).thenReturn(rentalType);
        Mockito.when(mapper.toDTO(rentalType)).thenReturn(rentalTypeDTO);

        RentalTypeDTO result = service.update(existingId, updateDTO);

        Assertions.assertNotNull(result);
    }

    @Test
    void updateShouldThrowExceptionWhenIdDoesNotExist() {
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.update(nonExistingId, updateDTO);
        });
    }

    @Test
    void updateShouldCallMapperUpdateEntity() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(rentalType));
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(rentalType)).thenReturn(rentalType);
        Mockito.when(mapper.toDTO(rentalType)).thenReturn(rentalTypeDTO);

        service.update(existingId, updateDTO);

        Mockito.verify(mapper).copyToEntity(updateDTO, rentalType);
    }

    @Test
    void updateShouldSetUpdatedBy() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(rentalType));
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(rentalType)).thenReturn(rentalType);
        Mockito.when(mapper.toDTO(rentalType)).thenReturn(rentalTypeDTO);

        service.update(existingId, updateDTO);

        Assertions.assertEquals("admin", rentalType.getUpdatedBy());
    }

    @Test
    void updateShouldNotChangeActiveStatus() {
        rentalType.setActive(false);

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(rentalType));
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(rentalType)).thenReturn(rentalType);
        Mockito.when(mapper.toDTO(rentalType)).thenReturn(rentalTypeDTO);

        service.update(existingId, updateDTO);

        Assertions.assertFalse(rentalType.getActive());
    }

    @Test
    void deleteShouldDoNothingWhenIdExists() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(rentalType));

        Assertions.assertDoesNotThrow(() -> service.delete(existingId));
    }

    @Test
    void deleteShouldThrowResourceNotFoundWhenIdDoesNotExist() {
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });
    }

    @Test
    void deleteShouldThrowDatabaseExceptionWhenIntegrityViolation() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(rentalType));
        Mockito.doThrow(DataIntegrityViolationException.class)
                .when(repository).flush();

        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(existingId);
        });
    }

    @Test
    void deleteAllShouldDeleteExistingIds() {
        List<Long> ids = List.of(existingId);

        Mockito.when(repository.findAllById(ids)).thenReturn(List.of(rentalType));

        service.deleteAll(ids);

        Mockito.verify(repository).deleteAllByIds(ids);
    }

    @Test
    void deleteAllShouldThrowIllegalArgumentExceptionWhenIdsAreNullOrEmpty() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            service.deleteAll(null);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            service.deleteAll(List.of());
        });
    }

    @Test
    void deleteAllShouldThrowResourceNotFoundWhenAnyIdDoesNotExist() {
        List<Long> ids = List.of(existingId, nonExistingId);

        Mockito.when(repository.findAllById(ids)).thenReturn(List.of(rentalType));

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.deleteAll(ids);
        });
    }

    @Test
    void deleteAllShouldThrowDatabaseExceptionWhenIntegrityViolation() {
        List<Long> ids = List.of(existingId);

        Mockito.when(repository.findAllById(ids)).thenReturn(List.of(rentalType));
        Mockito.doThrow(DataIntegrityViolationException.class)
                .when(repository).flush();

        Assertions.assertThrows(DatabaseException.class, () -> {
            service.deleteAll(ids);
        });
    }

    @Test
    void changeActiveStatusShouldUpdateWhenIdExists() {
        Mockito.when(repository.updateActiveById(existingId, false)).thenReturn(1);

        service.changeActiveStatus(existingId, false);

        Mockito.verify(repository).updateActiveById(existingId, false);
    }

    @Test
    void changeActiveStatusShouldThrowWhenIdDoesNotExist() {
        Mockito.when(repository.updateActiveById(nonExistingId, false)).thenReturn(0);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.changeActiveStatus(nonExistingId, false);
        });
    }

    @Test
    void changeActiveStatusShouldThrowWhenDataAccessFails() {
        Mockito.when(repository.updateActiveById(existingId, false))
                .thenThrow(new DataAccessResourceFailureException("fail"));

        Assertions.assertThrows(DatabaseException.class, () -> {
            service.changeActiveStatus(existingId, false);
        });
    }
}
