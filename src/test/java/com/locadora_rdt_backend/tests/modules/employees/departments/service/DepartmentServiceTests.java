package com.locadora_rdt_backend.tests.modules.employees.departments.service;

import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.employees.departments.dto.DepartmentDTO;
import com.locadora_rdt_backend.modules.employees.departments.dto.DepartmentDetailsDTO;
import com.locadora_rdt_backend.modules.employees.departments.dto.DepartmentInsertDTO;
import com.locadora_rdt_backend.modules.employees.departments.dto.DepartmentUpdateDTO;
import com.locadora_rdt_backend.modules.employees.departments.mapper.DepartmentMapper;
import com.locadora_rdt_backend.modules.employees.departments.model.Department;
import com.locadora_rdt_backend.modules.employees.departments.repository.DepartmentRepository;
import com.locadora_rdt_backend.modules.employees.departments.service.DepartmentService;
import com.locadora_rdt_backend.tests.modules.employees.departments.factory.DepartmentFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class DepartmentServiceTests {

    @InjectMocks
    private DepartmentService service;

    @Mock
    private DepartmentRepository repository;

    @Spy
    private DepartmentMapper mapper = new DepartmentMapper();

    private Long existingId;
    private Long nonExistingId;

    private Department department;
    private DepartmentInsertDTO insertDTO;
    private DepartmentUpdateDTO updateDTO;

    private PageImpl<Department> page;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 999L;

        department = DepartmentFactory.createDepartment();
        insertDTO = DepartmentFactory.createDepartmentInsertDTO();
        updateDTO = DepartmentFactory.createDepartmentUpdateDTO();

        page = new PageImpl<>(List.of(department));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void findAllPagedShouldReturnPageWhenNameExists() {
        String name = "TI";
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.find(name, pageRequest)).thenReturn(page);

        Page<DepartmentDTO> result = service.findAllPaged(name, pageRequest);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals(department.getId(), result.getContent().get(0).getId());
        Assertions.assertEquals(department.getName(), result.getContent().get(0).getName());

        Mockito.verify(repository, Mockito.times(1)).find(name, pageRequest);
        Mockito.verify(mapper, Mockito.times(1)).toDTO(department);
    }

    @Test
    void findAllPagedShouldReturnEmptyPageWhenNoData() {
        String name = "Financeiro";
        PageRequest pageRequest = PageRequest.of(0, 10);
        PageImpl<Department> emptyPage = new PageImpl<>(Collections.emptyList());

        Mockito.when(repository.find(name, pageRequest)).thenReturn(emptyPage);

        Page<DepartmentDTO> result = service.findAllPaged(name, pageRequest);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
        Assertions.assertEquals(0, result.getTotalElements());

        Mockito.verify(repository, Mockito.times(1)).find(name, pageRequest);
        Mockito.verify(mapper, Mockito.never()).toDTO(ArgumentMatchers.any(Department.class));
    }

    @Test
    void findAllPagedShouldCallRepositoryWithEmptyName() {
        String name = "";
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.find(name, pageRequest)).thenReturn(page);

        Page<DepartmentDTO> result = service.findAllPaged(name, pageRequest);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getTotalElements());

        Mockito.verify(repository, Mockito.times(1)).find(name, pageRequest);
        Mockito.verify(mapper, Mockito.times(1)).toDTO(department);
    }

    @Test
    void findByIdShouldReturnDTOWhenIdExists() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(department));

        DepartmentDetailsDTO result = service.findById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.getId());
        Assertions.assertEquals(department.getName(), result.getName());
        Assertions.assertEquals(department.getDescription(), result.getDescription());

        Mockito.verify(repository, Mockito.times(1)).findById(existingId);
        Mockito.verify(mapper, Mockito.times(1)).toDetailsDTO(department);
    }

    @Test
    void findByIdShouldThrowExceptionWhenIdDoesNotExist() {
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });

        Mockito.verify(repository, Mockito.times(1)).findById(nonExistingId);
        Mockito.verify(mapper, Mockito.never()).toDetailsDTO(ArgumentMatchers.any(Department.class));
    }

    @Test
    void findByIdShouldNotUseToDTOBecauseUsesDetailsDTO() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(department));

        service.findById(existingId);

        Mockito.verify(mapper, Mockito.times(1)).toDetailsDTO(department);
        Mockito.verify(mapper, Mockito.never()).toDTO(ArgumentMatchers.any(Department.class));
    }

    @Test
    void insertShouldReturnDTO() {
        Mockito.when(repository.save(ArgumentMatchers.any(Department.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DepartmentDTO result = service.insert(insertDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(insertDTO.getName(), result.getName());

        Mockito.verify(mapper, Mockito.times(1)).toEntity(insertDTO);
        Mockito.verify(repository, Mockito.times(1)).save(ArgumentMatchers.any(Department.class));
        Mockito.verify(mapper, Mockito.times(1)).toDTO(ArgumentMatchers.any(Department.class));
    }

    @Test
    void insertShouldSetCreatedBySystemWhenNoAuth() {
        Mockito.when(repository.save(ArgumentMatchers.any(Department.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.insert(insertDTO);

        ArgumentCaptor<Department> captor = ArgumentCaptor.forClass(Department.class);
        Mockito.verify(repository).save(captor.capture());

        Assertions.assertEquals("SYSTEM", captor.getValue().getCreatedBy());
    }

    @Test
    void insertShouldSetCreatedByAuthenticatedUser() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        "renan",
                        null,
                        Collections.emptyList()
                );

        SecurityContextHolder.getContext().setAuthentication(auth);

        Mockito.when(repository.save(ArgumentMatchers.any(Department.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.insert(insertDTO);

        ArgumentCaptor<Department> captor = ArgumentCaptor.forClass(Department.class);
        Mockito.verify(repository).save(captor.capture());

        Assertions.assertEquals("renan", captor.getValue().getCreatedBy());
    }

    @Test
    void updateShouldReturnDTOWhenIdExists() {
        Mockito.when(repository.getOne(existingId)).thenReturn(department);
        Mockito.when(repository.save(ArgumentMatchers.any(Department.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DepartmentDTO result = service.update(existingId, updateDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.getId());
        Assertions.assertEquals(updateDTO.getName(), result.getName());

        Mockito.verify(repository, Mockito.times(1)).getOne(existingId);
        Mockito.verify(mapper, Mockito.times(1)).updateEntity(department, updateDTO);
        Mockito.verify(repository, Mockito.times(1)).save(department);
        Mockito.verify(mapper, Mockito.times(1)).toDTO(department);
    }

    @Test
    void updateShouldThrowExceptionWhenIdDoesNotExist() {
        Mockito.when(repository.getOne(nonExistingId))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.update(nonExistingId, updateDTO);
        });

        Mockito.verify(repository, Mockito.times(1)).getOne(nonExistingId);
        Mockito.verify(repository, Mockito.never()).save(ArgumentMatchers.any(Department.class));
    }

    @Test
    void updateShouldSetUpdatedBySystemWhenNoAuth() {
        Mockito.when(repository.getOne(existingId)).thenReturn(department);
        Mockito.when(repository.save(ArgumentMatchers.any(Department.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.update(existingId, updateDTO);

        ArgumentCaptor<Department> captor = ArgumentCaptor.forClass(Department.class);
        Mockito.verify(repository).save(captor.capture());

        Assertions.assertEquals("SYSTEM", captor.getValue().getUpdatedBy());
    }

    @Test
    void deleteShouldDoNothingWhenIdExists() {
        Mockito.doNothing().when(repository).deleteById(existingId);

        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingId);
        });

        Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
    }

    @Test
    void deleteShouldThrowResourceNotFoundWhenIdDoesNotExist() {
        Mockito.doThrow(EmptyResultDataAccessException.class)
                .when(repository).deleteById(nonExistingId);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });

        Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
    }

    @Test
    void deleteShouldThrowDatabaseExceptionWhenIntegrityViolation() {
        Mockito.doThrow(DataIntegrityViolationException.class)
                .when(repository).deleteById(existingId);

        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(existingId);
        });

        Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
    }
}