package com.locadora_rdt_backend.tests.modules.employees.service;

import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.employees.departments.model.Department;
import com.locadora_rdt_backend.modules.employees.departments.repository.DepartmentRepository;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeDTO;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeDetailsDTO;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeInsertDTO;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeUpdateDTO;
import com.locadora_rdt_backend.modules.employees.mapper.EmployeeMapper;
import com.locadora_rdt_backend.modules.employees.model.Employee;
import com.locadora_rdt_backend.modules.employees.positions.model.Position;
import com.locadora_rdt_backend.modules.employees.positions.repository.PositionRepository;
import com.locadora_rdt_backend.modules.employees.repository.EmployeeRepository;
import com.locadora_rdt_backend.modules.employees.service.EmployeeService;
import com.locadora_rdt_backend.tests.factories.EmployeeFactory;
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
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTests {

    @InjectMocks
    private EmployeeService service;

    @Mock
    private EmployeeRepository repository;

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Spy
    private EmployeeMapper mapper = new EmployeeMapper();

    private Long existingId;
    private Long nonExistingId;

    private Employee employee;
    private EmployeeInsertDTO insertDTO;
    private EmployeeUpdateDTO updateDTO;

    private Position position;
    private Department department;

    private PageImpl<Employee> page;

    @BeforeEach
    void setUp() {

        existingId = 1L;
        nonExistingId = 999L;

        employee = EmployeeFactory.createEmployee();
        insertDTO = EmployeeFactory.createEmployeeInsertDTO();
        updateDTO = EmployeeFactory.createEmployeeUpdateDTO();

        position = EmployeeFactory.createPosition();
        department = EmployeeFactory.createDepartment();

        page = new PageImpl<>(List.of(employee));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void findAllPagedShouldReturnPageWhenNameExists() {

        String name = "João";
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.find(name, pageRequest)).thenReturn(page);

        Page<EmployeeDTO> result = service.findAllPaged(name, pageRequest);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals(employee.getId(), result.getContent().get(0).getId());

        Mockito.verify(repository, Mockito.times(1))
                .find(name, pageRequest);

        Mockito.verify(mapper, Mockito.times(1))
                .toDTO(employee);
    }

    @Test
    void findAllPagedShouldReturnEmptyPageWhenNoData() {

        String name = "Teste";
        PageRequest pageRequest = PageRequest.of(0, 10);

        PageImpl<Employee> emptyPage =
                new PageImpl<>(Collections.emptyList());

        Mockito.when(repository.find(name, pageRequest))
                .thenReturn(emptyPage);

        Page<EmployeeDTO> result =
                service.findAllPaged(name, pageRequest);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());

        Mockito.verify(repository, Mockito.times(1))
                .find(name, pageRequest);

        Mockito.verify(mapper, Mockito.never())
                .toDTO(ArgumentMatchers.any(Employee.class));
    }

    @Test
    void findAllPagedShouldCallRepositoryWithEmptyName() {

        String name = "";
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.find(name, pageRequest))
                .thenReturn(page);

        Page<EmployeeDTO> result =
                service.findAllPaged(name, pageRequest);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getTotalElements());

        Mockito.verify(repository, Mockito.times(1))
                .find(name, pageRequest);
    }

    @Test
    void findByIdShouldReturnDTOWhenIdExists() {

        Mockito.when(repository.findById(existingId))
                .thenReturn(Optional.of(employee));

        EmployeeDetailsDTO result = service.findById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.getId());
        Assertions.assertEquals(employee.getName(), result.getName());

        Mockito.verify(repository, Mockito.times(1))
                .findById(existingId);

        Mockito.verify(mapper, Mockito.times(1))
                .toDetailsDTO(employee);
    }

    @Test
    void findByIdShouldThrowExceptionWhenIdDoesNotExist() {

        Mockito.when(repository.findById(nonExistingId))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });

        Mockito.verify(repository, Mockito.times(1))
                .findById(nonExistingId);
    }

    @Test
    void findByIdShouldNotUseToDTOBecauseUsesDetailsDTO() {

        Mockito.when(repository.findById(existingId))
                .thenReturn(Optional.of(employee));

        service.findById(existingId);

        Mockito.verify(mapper, Mockito.times(1))
                .toDetailsDTO(employee);

        Mockito.verify(mapper, Mockito.never())
                .toDTO(ArgumentMatchers.any(Employee.class));
    }

    @Test
    void insertShouldReturnDTO() {

        Mockito.when(positionRepository.getOne(insertDTO.getPositionId()))
                .thenReturn(position);

        Mockito.when(departmentRepository.getOne(insertDTO.getDepartmentId()))
                .thenReturn(department);

        Mockito.when(repository.save(ArgumentMatchers.any(Employee.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EmployeeDTO result = service.insert(insertDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(insertDTO.getName(), result.getName());

        Mockito.verify(mapper, Mockito.times(1))
                .toEntity(insertDTO);

        Mockito.verify(repository, Mockito.times(1))
                .save(ArgumentMatchers.any(Employee.class));

        Mockito.verify(mapper, Mockito.times(1))
                .toDTO(ArgumentMatchers.any(Employee.class));
    }

    @Test
    void insertShouldSetCreatedBySystemWhenNoAuth() {

        Mockito.when(positionRepository.getOne(insertDTO.getPositionId()))
                .thenReturn(position);

        Mockito.when(departmentRepository.getOne(insertDTO.getDepartmentId()))
                .thenReturn(department);

        Mockito.when(repository.save(ArgumentMatchers.any(Employee.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.insert(insertDTO);

        ArgumentCaptor<Employee> captor =
                ArgumentCaptor.forClass(Employee.class);

        Mockito.verify(repository).save(captor.capture());

        Assertions.assertEquals(
                "SYSTEM",
                captor.getValue().getCreatedBy()
        );
    }

    @Test
    void insertShouldThrowResourceNotFoundWhenPositionDoesNotExist() {

        Mockito.when(positionRepository.getOne(insertDTO.getPositionId()))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.insert(insertDTO);
        });

        Mockito.verify(repository, Mockito.never())
                .save(ArgumentMatchers.any(Employee.class));
    }

    @Test
    void updateShouldReturnDTOWhenIdExists() {

        Mockito.when(repository.getOne(existingId))
                .thenReturn(employee);

        Mockito.when(positionRepository.getOne(updateDTO.getPositionId()))
                .thenReturn(position);

        Mockito.when(departmentRepository.getOne(updateDTO.getDepartmentId()))
                .thenReturn(department);

        Mockito.when(repository.save(ArgumentMatchers.any(Employee.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EmployeeDTO result = service.update(existingId, updateDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.getId());

        Mockito.verify(repository, Mockito.times(1))
                .getOne(existingId);

        Mockito.verify(mapper, Mockito.times(1))
                .updateEntity(employee, updateDTO);

        Mockito.verify(repository, Mockito.times(1))
                .save(employee);
    }

    @Test
    void updateShouldThrowExceptionWhenIdDoesNotExist() {

        Mockito.when(repository.getOne(nonExistingId))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.update(nonExistingId, updateDTO);
        });

        Mockito.verify(repository, Mockito.times(1))
                .getOne(nonExistingId);
    }

    @Test
    void updateShouldSetUpdatedByAuthenticatedUser() {

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        "renan",
                        null,
                        Collections.emptyList()
                );

        SecurityContextHolder.getContext().setAuthentication(auth);

        Mockito.when(repository.getOne(existingId))
                .thenReturn(employee);

        Mockito.when(positionRepository.getOne(updateDTO.getPositionId()))
                .thenReturn(position);

        Mockito.when(departmentRepository.getOne(updateDTO.getDepartmentId()))
                .thenReturn(department);

        Mockito.when(repository.save(ArgumentMatchers.any(Employee.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.update(existingId, updateDTO);

        ArgumentCaptor<Employee> captor =
                ArgumentCaptor.forClass(Employee.class);

        Mockito.verify(repository).save(captor.capture());

        Assertions.assertEquals(
                "renan",
                captor.getValue().getUpdatedBy()
        );
    }

    @Test
    void updatePhotoShouldUpdatePhotoWhenFileIsValid() {

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "photo.png",
                        "image/png",
                        "fake-image".getBytes()
                );

        Mockito.when(repository.findById(existingId))
                .thenReturn(Optional.of(employee));

        service.updatePhoto(existingId, file);

        Mockito.verify(repository, Mockito.times(1))
                .save(employee);

        Assertions.assertNotNull(employee.getPhoto());
        Assertions.assertEquals("image/png", employee.getPhotoContentType());
    }

    @Test
    void updatePhotoShouldThrowExceptionWhenEmployeeDoesNotExist() {

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "photo.png",
                        "image/png",
                        "fake-image".getBytes()
                );

        Mockito.when(repository.findById(nonExistingId))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.updatePhoto(nonExistingId, file);
        });
    }

    @Test
    void updatePhotoShouldThrowExceptionWhenFileIsInvalid() {

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "photo.txt",
                        "text/plain",
                        "fake-image".getBytes()
                );

        Mockito.when(repository.findById(existingId))
                .thenReturn(Optional.of(employee));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            service.updatePhoto(existingId, file);
        });
    }

    @Test
    void findEntityByIdShouldReturnEntityWhenIdExists() {

        Mockito.when(repository.findById(existingId))
                .thenReturn(Optional.of(employee));

        Employee result = service.findEntityById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.getId());
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
    void findEntityByIdShouldCallRepositoryOnce() {

        Mockito.when(repository.findById(existingId))
                .thenReturn(Optional.of(employee));

        service.findEntityById(existingId);

        Mockito.verify(repository, Mockito.times(1))
                .findById(existingId);
    }

    @Test
    void deleteShouldDoNothingWhenIdExists() {

        Mockito.doNothing()
                .when(repository)
                .deleteById(existingId);

        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingId);
        });

        Mockito.verify(repository, Mockito.times(1))
                .deleteById(existingId);
    }

    @Test
    void deleteShouldThrowExceptionWhenIdDoesNotExist() {

        Mockito.doThrow(EmptyResultDataAccessException.class)
                .when(repository)
                .deleteById(nonExistingId);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });
    }

    @Test
    void deleteShouldCallRepositoryDeleteById() {

        Mockito.doNothing()
                .when(repository)
                .deleteById(existingId);

        service.delete(existingId);

        Mockito.verify(repository, Mockito.times(1))
                .deleteById(existingId);
    }

    @Test
    void deleteAllShouldDeleteAllWhenIdsExist() {

        List<Long> ids = List.of(1L);

        Mockito.when(repository.findAllById(ids))
                .thenReturn(List.of(employee));

        Mockito.doNothing()
                .when(repository)
                .deleteAllByIds(ids);

        Assertions.assertDoesNotThrow(() -> {
            service.deleteAll(ids);
        });

        Mockito.verify(repository, Mockito.times(1))
                .deleteAllByIds(ids);
    }

    @Test
    void deleteAllShouldThrowExceptionWhenListIsEmpty() {

        List<Long> ids = Collections.emptyList();

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            service.deleteAll(ids);
        });
    }

    @Test
    void deleteAllShouldThrowExceptionWhenSomeIdsDoNotExist() {

        List<Long> ids = List.of(1L, 2L);

        Mockito.when(repository.findAllById(ids))
                .thenReturn(List.of(employee));

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.deleteAll(ids);
        });
    }

    @Test
    void changeActiveStatusShouldUpdateStatusWhenIdExists() {

        Mockito.when(repository.updateActiveById(existingId, true))
                .thenReturn(1);

        Assertions.assertDoesNotThrow(() -> {
            service.changeActiveStatus(existingId, true);
        });

        Mockito.verify(repository, Mockito.times(1))
                .updateActiveById(existingId, true);
    }

    @Test
    void changeActiveStatusShouldThrowExceptionWhenIdDoesNotExist() {

        Mockito.when(repository.updateActiveById(nonExistingId, true))
                .thenReturn(0);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.changeActiveStatus(nonExistingId, true);
        });
    }

    @Test
    void changeActiveStatusShouldThrowRuntimeExceptionWhenDatabaseFails() {

        Mockito.when(repository.updateActiveById(existingId, true))
                .thenThrow(new DataIntegrityViolationException("Database error"));

        Assertions.assertThrows(RuntimeException.class, () -> {
            service.changeActiveStatus(existingId, true);
        });
    }
}