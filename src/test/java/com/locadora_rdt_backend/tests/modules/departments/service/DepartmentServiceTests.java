package com.locadora_rdt_backend.tests.modules.departments.service;

import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.departments.dto.DepartmentDTO;
import com.locadora_rdt_backend.modules.departments.dto.DepartmentDetailsDTO;
import com.locadora_rdt_backend.modules.departments.dto.DepartmentInsertDTO;
import com.locadora_rdt_backend.modules.departments.dto.DepartmentUpdateDTO;
import com.locadora_rdt_backend.modules.departments.mapper.DepartmentMapper;
import com.locadora_rdt_backend.modules.departments.model.Department;
import com.locadora_rdt_backend.modules.departments.repository.DepartmentRepository;
import com.locadora_rdt_backend.modules.departments.service.DepartmentServiceImpl;
import com.locadora_rdt_backend.tests.modules.departments.factory.DepartmentFactory;
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

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class DepartmentServiceTests {

    @InjectMocks
    private DepartmentServiceImpl service;

    @Mock
    private DepartmentRepository repository;

    @Mock
    private DepartmentMapper mapper;

    @Mock
    private AuthenticationFacade authenticationFacade;

    private Long existingId;
    private Long nonExistingId;

    private Department department;
    private DepartmentDTO departmentDTO;
    private DepartmentDetailsDTO departmentDetailsDTO;
    private DepartmentInsertDTO insertDTO;
    private DepartmentUpdateDTO updateDTO;
    private PageImpl<Department> page;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 999L;

        department = DepartmentFactory.createDepartment();
        departmentDTO = DepartmentFactory.createDepartmentDTO(department);
        departmentDetailsDTO = DepartmentFactory.createDepartmentDetailsDTO(department);
        insertDTO = DepartmentFactory.createDepartmentInsertDTO();
        updateDTO = DepartmentFactory.createDepartmentUpdateDTO();
        page = new PageImpl<>(java.util.List.of(department));
    }

    @Test
    void findAllPagedShouldReturnPage() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.find("", pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(department)).thenReturn(departmentDTO);

        Page<DepartmentDTO> result = service.findAllPaged("", pageRequest);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    void findAllPagedShouldNormalizeNullName() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.find("", pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(department)).thenReturn(departmentDTO);

        service.findAllPaged(null, pageRequest);

        Mockito.verify(repository).find("", pageRequest);
    }

    @Test
    void findAllPagedShouldNormalizeBlankName() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.find("Tecnologia", pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(department)).thenReturn(departmentDTO);

        service.findAllPaged(" Tecnologia ", pageRequest);

        Mockito.verify(repository).find("Tecnologia", pageRequest);
    }

    @Test
    void findAllPagedShouldReturnEmptyPageWhenNoData() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        PageImpl<Department> emptyPage = new PageImpl<>(java.util.List.of());

        Mockito.when(repository.find("", pageRequest)).thenReturn(emptyPage);

        Page<DepartmentDTO> result = service.findAllPaged("", pageRequest);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void findByIdShouldReturnDTOWhenIdExists() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(department));
        Mockito.when(mapper.toDetailsDTO(department)).thenReturn(departmentDetailsDTO);

        DepartmentDetailsDTO result = service.findById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.getId());
    }

    @Test
    void findByIdShouldThrowExceptionWhenIdDoesNotExist() {
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });
    }

    @Test
    void insertShouldReturnDTO() {
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(department);
        Mockito.when(repository.save(department)).thenReturn(department);
        Mockito.when(mapper.toDTO(department)).thenReturn(departmentDTO);

        DepartmentDTO result = service.insert(insertDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(department.getName(), result.getName());
    }

    @Test
    void insertShouldSetCreatedBy() {
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(department);
        Mockito.when(repository.save(department)).thenReturn(department);
        Mockito.when(mapper.toDTO(department)).thenReturn(departmentDTO);

        service.insert(insertDTO);

        Assertions.assertEquals("admin", department.getCreatedBy());
    }

    @Test
    void updateShouldReturnDTOWhenIdExists() {
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(department));
        Mockito.when(repository.save(department)).thenReturn(department);
        Mockito.when(mapper.toDTO(department)).thenReturn(departmentDTO);

        DepartmentDTO result = service.update(existingId, updateDTO);

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
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(department));
        Mockito.when(repository.save(department)).thenReturn(department);
        Mockito.when(mapper.toDTO(department)).thenReturn(departmentDTO);

        service.update(existingId, updateDTO);

        Mockito.verify(mapper).updateEntity(department, updateDTO);
    }

    @Test
    void deleteShouldDoNothingWhenIdExists() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(department));

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
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(department));
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).flush();

        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(existingId);
        });
    }
}
