package com.locadora_rdt_backend.tests.modules.employees.positions.service;

import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.employees.positions.dto.*;
import com.locadora_rdt_backend.modules.employees.positions.mapper.PositionMapper;
import com.locadora_rdt_backend.modules.employees.positions.model.Position;
import com.locadora_rdt_backend.modules.employees.positions.repository.PositionRepository;
import com.locadora_rdt_backend.modules.employees.positions.service.PositionService;


import com.locadora_rdt_backend.tests.modules.employees.positions.factory.PositionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.*;

import javax.persistence.EntityNotFoundException;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PositionServiceTests {

    @InjectMocks
    private PositionService service;

    @Mock
    private PositionRepository repository;

    @Mock
    private PositionMapper mapper;

    private Long existingId;
    private Long nonExistingId;

    private Position position;
    private PositionDTO positionDTO;
    private PositionDetailsDTO positionDetailsDTO;
    private PositionInsertDTO insertDTO;
    private PositionUpdateDTO updateDTO;

    private PageImpl<Position> page;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 999L;

        position = PositionFactory.createPosition();
        positionDTO = PositionFactory.createPositionDTO(position);
        positionDetailsDTO = PositionFactory.createPositionDetailsDTO(position);
        insertDTO = PositionFactory.createPositionInsertDTO();
        updateDTO = PositionFactory.createPositionUpdateDTO();

        page = new PageImpl<>(java.util.List.of(position));
    }

    @Test
    void findAllPagedShouldReturnPage() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.find("", pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(position)).thenReturn(positionDTO);

        Page<PositionDTO> result = service.findAllPaged("", pageRequest);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    void findAllPagedShouldReturnEmptyPageWhenNoData() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        PageImpl<Position> emptyPage = new PageImpl<>(java.util.List.of());

        Mockito.when(repository.find("", pageRequest)).thenReturn(emptyPage);

        Page<PositionDTO> result = service.findAllPaged("", pageRequest);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void findAllPagedShouldCallRepository() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.find("", pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(position)).thenReturn(positionDTO);

        service.findAllPaged("", pageRequest);

        Mockito.verify(repository).find("", pageRequest);
    }

    @Test
    void findByIdShouldReturnDTOWhenIdExists() {
        Mockito.when(repository.findById(existingId))
                .thenReturn(Optional.of(position));

        Mockito.when(mapper.toDetailsDTO(position))
                .thenReturn(positionDetailsDTO);

        PositionDetailsDTO result = service.findById(existingId);

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
                .thenReturn(Optional.of(position));

        Mockito.when(mapper.toDetailsDTO(position))
                .thenReturn(positionDetailsDTO);

        service.findById(existingId);

        Mockito.verify(repository).findById(existingId);
    }


    @Test
    void insertShouldReturnDTO() {
        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(position);
        Mockito.when(repository.save(position)).thenReturn(position);
        Mockito.when(mapper.toDTO(position)).thenReturn(positionDTO);

        PositionDTO result = service.insert(insertDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(position.getName(), result.getName());
    }

    @Test
    void insertShouldCallRepositorySave() {
        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(position);
        Mockito.when(repository.save(position)).thenReturn(position);
        Mockito.when(mapper.toDTO(position)).thenReturn(positionDTO);

        service.insert(insertDTO);

        Mockito.verify(repository).save(position);
    }

    @Test
    void insertShouldMapEntityCorrectly() {
        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(position);
        Mockito.when(repository.save(position)).thenReturn(position);
        Mockito.when(mapper.toDTO(position)).thenReturn(positionDTO);

        service.insert(insertDTO);

        Mockito.verify(mapper).toEntity(insertDTO);
    }

    @Test
    void updateShouldReturnDTOWhenIdExists() {
        Mockito.when(repository.getOne(existingId)).thenReturn(position);
        Mockito.when(repository.save(position)).thenReturn(position);
        Mockito.when(mapper.toDTO(position)).thenReturn(positionDTO);

        PositionDTO result = service.update(existingId, updateDTO);

        Assertions.assertNotNull(result);
    }

    @Test
    void updateShouldThrowExceptionWhenIdDoesNotExist() {
        Mockito.when(repository.getOne(nonExistingId))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.update(nonExistingId, updateDTO);
        });
    }

    @Test
    void updateShouldCallMapperUpdateEntity() {
        Mockito.when(repository.getOne(existingId)).thenReturn(position);
        Mockito.when(repository.save(position)).thenReturn(position);
        Mockito.when(mapper.toDTO(position)).thenReturn(positionDTO);

        service.update(existingId, updateDTO);

        Mockito.verify(mapper).updateEntity(position, updateDTO);
    }

    @Test
    void deleteShouldDoNothingWhenIdExists() {
        Mockito.doNothing().when(repository).deleteById(existingId);

        Assertions.assertDoesNotThrow(() -> service.delete(existingId));
    }

    @Test
    void deleteShouldThrowResourceNotFoundWhenIdDoesNotExist() {
        Mockito.doThrow(EmptyResultDataAccessException.class)
                .when(repository).deleteById(nonExistingId);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });
    }

    @Test
    void deleteShouldThrowDatabaseExceptionWhenIntegrityViolation() {
        Mockito.doThrow(DataIntegrityViolationException.class)
                .when(repository).deleteById(existingId);

        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(existingId);
        });
    }
}