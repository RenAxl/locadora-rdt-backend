package com.locadora_rdt_backend.tests.modules.employees.positions.service;

import com.locadora_rdt_backend.modules.employees.positions.dto.PositionDTO;
import com.locadora_rdt_backend.modules.employees.positions.model.Position;
import com.locadora_rdt_backend.modules.employees.positions.repository.PositionRepository;
import com.locadora_rdt_backend.modules.employees.positions.service.PositionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
public class PositionServiceTests {

    @InjectMocks
    private PositionService service;

    @Mock
    private PositionRepository repository;

    private String existingName;
    private String nonExistingName;
    private Position position;
    private PageImpl<Position> page;
    private PageImpl<Position> emptyPage;
    private PositionDTO dto;

    @BeforeEach
    void setUp() {

        existingName = "Gerente";
        nonExistingName = "Inexistente";

        position = new Position();
        position.setId(1L);
        position.setName("Gerente");

        dto = new PositionDTO();
        dto.setName("Gerente");

        page = new PageImpl<>(List.of(position));
        emptyPage = new PageImpl<>(List.of());

        Mockito.when(repository.find(ArgumentMatchers.eq(existingName), ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(page);

        Mockito.when(repository.find(ArgumentMatchers.eq(nonExistingName), ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(emptyPage);

        Mockito.when(repository.find(ArgumentMatchers.eq(""), ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(page);

        Mockito.when(repository.save(ArgumentMatchers.any(Position.class)))
                .thenAnswer(invocation -> {
                    Position entity = invocation.getArgument(0);
                    entity.setId(1L);
                    return entity;
                });
    }


    @Test
    public void findAllPagedShouldReturnPageWhenNameExists() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<PositionDTO> result = service.findAllPaged(existingName, pageRequest);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.getTotalElements());

        Mockito.verify(repository, Mockito.times(1)).find(existingName, pageRequest);
    }

    @Test
    public void findAllPagedShouldReturnEmptyPageWhenNameDoesNotExist() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<PositionDTO> result = service.findAllPaged(nonExistingName, pageRequest);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
        Assertions.assertEquals(0, result.getTotalElements());

        Mockito.verify(repository, Mockito.times(1)).find(nonExistingName, pageRequest);
    }

    @Test
    public void findAllPagedShouldMapEntityToDTO() {
        String name = "";
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<PositionDTO> result = service.findAllPaged(name, pageRequest);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(position.getId(), result.getContent().get(0).getId());
        Assertions.assertEquals(position.getName(), result.getContent().get(0).getName());

        Mockito.verify(repository, Mockito.times(1)).find(name, pageRequest);
    }

    @Test
    public void findByIdShouldReturnDTOWhenIdExists() {
        Long existingId = 1L;

        Mockito.when(repository.findById(existingId))
                .thenReturn(java.util.Optional.of(position));

        PositionDTO result = service.findById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.getId());
        Assertions.assertEquals(position.getName(), result.getName());

        Mockito.verify(repository).findById(existingId);
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Long nonExistingId = 999L;

        Mockito.when(repository.findById(nonExistingId))
                .thenReturn(java.util.Optional.empty());

        Assertions.assertThrows(
                com.locadora_rdt_backend.common.exception.ResourceNotFoundException.class,
                () -> service.findById(nonExistingId)
        );

        Mockito.verify(repository).findById(nonExistingId);
    }


    @Test
    public void insertShouldSavePositionAndReturnDTO() {
        PositionDTO result = service.insert(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals(dto.getName(), result.getName());

        Mockito.verify(repository, Mockito.times(1)).save(ArgumentMatchers.any(Position.class));
    }

    @Test
    public void insertShouldCopyDtoDataToEntity() {
        service.insert(dto);

        ArgumentCaptor<Position> captor = ArgumentCaptor.forClass(Position.class);
        Mockito.verify(repository).save(captor.capture());

        Position savedEntity = captor.getValue();

        Assertions.assertEquals(dto.getName(), savedEntity.getName());
    }

    @Test
    public void insertShouldSetIdAfterSave() {
        PositionDTO result = service.insert(dto);

        Assertions.assertNotNull(result.getId());
        Assertions.assertEquals(1L, result.getId());
    }

    @Test
    public void updateShouldReturnDTOWhenIdExists() {
        Long existingId = 1L;

        Mockito.when(repository.getOne(existingId)).thenReturn(position);
        Mockito.when(repository.save(position)).thenReturn(position);

        PositionDTO result = service.update(existingId, dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.getId());
        Assertions.assertEquals(dto.getName(), result.getName());

        Mockito.verify(repository).getOne(existingId);
        Mockito.verify(repository).save(position);
    }

    @Test
    public void updateShouldUpdateEntityFields() {
        Long existingId = 1L;

        Mockito.when(repository.getOne(existingId)).thenReturn(position);
        Mockito.when(repository.save(position)).thenReturn(position);

        service.update(existingId, dto);

        Assertions.assertEquals(dto.getName(), position.getName());
        Assertions.assertNotNull(position.getUpdatedAt());
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Long nonExistingId = 999L;

        Mockito.when(repository.getOne(nonExistingId))
                .thenThrow(javax.persistence.EntityNotFoundException.class);

        Assertions.assertThrows(
                com.locadora_rdt_backend.common.exception.ResourceNotFoundException.class,
                () -> service.update(nonExistingId, dto)
        );

        Mockito.verify(repository).getOne(nonExistingId);
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {
        Long existingId = 1L;

        Mockito.doNothing().when(repository).deleteById(existingId);

        Assertions.assertDoesNotThrow(() -> service.delete(existingId));

        Mockito.verify(repository).deleteById(existingId);
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Long nonExistingId = 999L;

        Mockito.doThrow(org.springframework.dao.EmptyResultDataAccessException.class)
                .when(repository).deleteById(nonExistingId);

        Assertions.assertThrows(
                com.locadora_rdt_backend.common.exception.ResourceNotFoundException.class,
                () -> service.delete(nonExistingId)
        );

        Mockito.verify(repository).deleteById(nonExistingId);
    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenIntegrityViolationOccurs() {
        Long dependentId = 2L;

        Mockito.doThrow(org.springframework.dao.DataIntegrityViolationException.class)
                .when(repository).deleteById(dependentId);

        Assertions.assertThrows(
                com.locadora_rdt_backend.common.exception.DatabaseException.class,
                () -> service.delete(dependentId)
        );

        Mockito.verify(repository).deleteById(dependentId);
    }
}