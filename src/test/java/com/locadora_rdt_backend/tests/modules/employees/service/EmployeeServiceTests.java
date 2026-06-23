package com.locadora_rdt_backend.tests.modules.employees.service;

import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.departments.model.Department;
import com.locadora_rdt_backend.modules.departments.service.DepartmentService;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeDTO;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeDetailsDTO;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeInsertDTO;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeUpdateDTO;
import com.locadora_rdt_backend.modules.employees.mapper.EmployeeMapper;
import com.locadora_rdt_backend.modules.employees.model.Employee;
import com.locadora_rdt_backend.modules.employees.repository.EmployeeRepository;
import com.locadora_rdt_backend.modules.employees.service.EmployeeServiceImpl;
import com.locadora_rdt_backend.modules.positions.model.Position;
import com.locadora_rdt_backend.modules.positions.service.PositionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTests {

    @InjectMocks
    private EmployeeServiceImpl service;

    @Mock
    private EmployeeRepository repository;

    @Mock
    private PositionService positionService;

    @Mock
    private DepartmentService departmentService;

    @Mock
    private EmployeeMapper mapper;

    @Mock
    private AuthenticationFacade authenticationFacade;

    private Long existingId;
    private Long nonExistingId;
    private Employee employee;
    private EmployeeDTO employeeDTO;
    private EmployeeDetailsDTO detailsDTO;
    private EmployeeInsertDTO insertDTO;
    private EmployeeUpdateDTO updateDTO;
    private Position position;
    private Department department;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 999L;

        position = new Position(1L, "Analista");
        department = new Department(1L, "TI", "Tecnologia");

        employee = new Employee();
        employee.setId(existingId);
        employee.setName("Funcionario");
        employee.setEmployeeCode("EMP001");
        employee.setEmail("funcionario@email.com");
        employee.setPhone("11999999999");
        employee.setAddress("Rua A");
        employee.setSalary(BigDecimal.valueOf(5000));
        employee.setHireDate(LocalDate.of(2026, 1, 1));
        employee.setEmploymentType("CLT");
        employee.setActive(true);
        employee.setPosition(position);
        employee.setDepartment(department);

        employeeDTO = new EmployeeDTO();
        employeeDTO.setId(existingId);
        employeeDTO.setName("Funcionario");
        employeeDTO.setEmployeeCode("EMP001");
        employeeDTO.setEmail("funcionario@email.com");
        employeeDTO.setActive(true);

        detailsDTO = new EmployeeDetailsDTO();
        detailsDTO.setId(existingId);
        detailsDTO.setName("Funcionario");
        detailsDTO.setEmployeeCode("EMP001");
        detailsDTO.setEmail("funcionario@email.com");

        insertDTO = new EmployeeInsertDTO();
        fill(insertDTO);

        updateDTO = new EmployeeUpdateDTO();
        fill(updateDTO);
        updateDTO.setId(existingId);
    }

    @Test
    void findAllPagedShouldReturnPage() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        PageImpl<Employee> page = new PageImpl<>(List.of(employee));

        Mockito.when(repository.find("", pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(employee)).thenReturn(employeeDTO);

        Page<EmployeeDTO> result = service.findAllPaged("", pageRequest);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(existingId, result.getContent().get(0).getId());
    }

    @Test
    void findByIdShouldReturnDetailsWhenIdExists() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(employee));
        Mockito.when(mapper.toDetailsDTO(employee)).thenReturn(detailsDTO);

        EmployeeDetailsDTO result = service.findById(existingId);

        Assertions.assertEquals(existingId, result.getId());
    }

    @Test
    void findByIdShouldThrowWhenIdDoesNotExist() {
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(nonExistingId));
    }

    @Test
    void insertShouldSaveEmployeeWithPositionDepartmentAndCreatedBy() {
        Employee newEmployee = new Employee();

        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(newEmployee);
        Mockito.when(positionService.findEntityById(1L)).thenReturn(position);
        Mockito.when(departmentService.findEntityById(1L)).thenReturn(department);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(newEmployee)).thenReturn(employee);
        Mockito.when(mapper.toDTO(employee)).thenReturn(employeeDTO);

        EmployeeDTO result = service.insert(insertDTO);

        Assertions.assertEquals(existingId, result.getId());
        Assertions.assertEquals(position, newEmployee.getPosition());
        Assertions.assertEquals(department, newEmployee.getDepartment());
        Assertions.assertEquals("admin", newEmployee.getCreatedBy());
    }

    @Test
    void insertShouldThrowWhenPositionOrDepartmentDoesNotExist() {
        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(new Employee());
        Mockito.when(positionService.findEntityById(1L)).thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.insert(insertDTO));
    }

    @Test
    void updateShouldReturnDTOWhenIdExists() {
        Mockito.when(repository.getOne(existingId)).thenReturn(employee);
        Mockito.when(positionService.findEntityById(1L)).thenReturn(position);
        Mockito.when(departmentService.findEntityById(1L)).thenReturn(department);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(employee)).thenReturn(employee);
        Mockito.when(mapper.toDTO(employee)).thenReturn(employeeDTO);

        EmployeeDTO result = service.update(existingId, updateDTO);

        Assertions.assertEquals(existingId, result.getId());
        Assertions.assertEquals(position, employee.getPosition());
        Assertions.assertEquals(department, employee.getDepartment());
        Assertions.assertEquals("admin", employee.getUpdatedBy());
        Mockito.verify(mapper).updateEntity(employee, updateDTO);
    }

    @Test
    void updateShouldThrowWhenEmployeePositionOrDepartmentDoesNotExist() {
        Mockito.when(repository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(nonExistingId, updateDTO));
    }

    @Test
    void updatePhotoShouldSaveValidPhoto() {
        MockMultipartFile file = new MockMultipartFile("file", "photo.png", "image/png", new byte[]{1});

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(employee));
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");

        service.updatePhoto(existingId, file);

        Assertions.assertArrayEquals(new byte[]{1}, employee.getPhoto());
        Assertions.assertEquals("image/png", employee.getPhotoContentType());
        Assertions.assertEquals("admin", employee.getUpdatedBy());
        Mockito.verify(repository).save(employee);
    }

    @Test
    void updatePhotoShouldThrowWhenEmployeeDoesNotExist() {
        MockMultipartFile file = new MockMultipartFile("file", "photo.png", "image/png", new byte[]{1});

        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.updatePhoto(nonExistingId, file));
    }

    @Test
    void updatePhotoShouldThrowWhenFileIsNullOrEmpty() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(employee));

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.updatePhoto(existingId, null));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.updatePhoto(existingId, new MockMultipartFile("file", new byte[]{})));
    }

    @Test
    void updatePhotoShouldThrowWhenFileTypeIsInvalid() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(employee));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.updatePhoto(existingId,
                        new MockMultipartFile("file", "photo.gif", "image/gif", new byte[]{1})));
    }

    @Test
    void updatePhotoShouldThrowWhenFileContentTypeIsNull() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(employee));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.updatePhoto(existingId,
                        new MockMultipartFile("file", "photo", null, new byte[]{1})));
    }

    @Test
    void updatePhotoShouldThrowWhenFileIsTooLarge() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(employee));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.updatePhoto(existingId,
                        new MockMultipartFile("file", "photo.png", "image/png", new byte[2 * 1024 * 1024 + 1])));
    }

    @Test
    void updatePhotoShouldThrowWhenFileReadFails() throws IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(employee));
        Mockito.when(file.isEmpty()).thenReturn(false);
        Mockito.when(file.getContentType()).thenReturn("image/png");
        Mockito.when(file.getSize()).thenReturn(1L);
        Mockito.when(file.getBytes()).thenThrow(IOException.class);

        Assertions.assertThrows(RuntimeException.class, () -> service.updatePhoto(existingId, file));
    }

    @Test
    void findEntityByIdShouldReturnEntityWhenIdExists() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(employee));

        Assertions.assertEquals(employee, service.findEntityById(existingId));
    }

    @Test
    void findEntityByIdShouldThrowWhenIdDoesNotExist() {
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findEntityById(nonExistingId));
    }

    @Test
    void deleteShouldDeleteWhenIdExists() {
        service.delete(existingId);

        Mockito.verify(repository).deleteById(existingId);
    }

    @Test
    void deleteShouldThrowWhenIdDoesNotExist() {
        Mockito.doThrow(new EmptyResultDataAccessException(1)).when(repository).deleteById(nonExistingId);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistingId));
    }

    @Test
    void deleteAllShouldValidateIds() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.deleteAll(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.deleteAll(List.of()));

        Mockito.when(repository.findAllById(List.of(existingId, nonExistingId))).thenReturn(List.of(employee));

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> service.deleteAll(List.of(existingId, nonExistingId)));
    }

    @Test
    void deleteAllShouldDeleteExistingIds() {
        Mockito.when(repository.findAllById(List.of(existingId))).thenReturn(List.of(employee));

        service.deleteAll(List.of(existingId));

        Mockito.verify(repository).deleteAllByIds(List.of(existingId));
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

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> service.changeActiveStatus(nonExistingId, false));
    }

    @Test
    void changeActiveStatusShouldThrowWhenDataAccessFails() {
        Mockito.when(repository.updateActiveById(existingId, false))
                .thenThrow(new DataAccessResourceFailureException("fail"));

        Assertions.assertThrows(RuntimeException.class, () -> service.changeActiveStatus(existingId, false));
    }

    private void fill(EmployeeInsertDTO dto) {
        dto.setName("Funcionario");
        dto.setEmployeeCode("EMP001");
        dto.setEmail("funcionario@email.com");
        dto.setPhone("11999999999");
        dto.setAddress("Rua A");
        dto.setSalary(BigDecimal.valueOf(5000));
        dto.setHireDate(LocalDate.of(2026, 1, 1));
        dto.setEmploymentType("CLT");
        dto.setActive(true);
        dto.setPositionId(1L);
        dto.setDepartmentId(1L);
    }

    private void fill(EmployeeUpdateDTO dto) {
        dto.setName("Funcionario Atualizado");
        dto.setEmployeeCode("EMP001");
        dto.setEmail("funcionario@email.com");
        dto.setPhone("11888888888");
        dto.setAddress("Rua B");
        dto.setSalary(BigDecimal.valueOf(6000));
        dto.setHireDate(LocalDate.of(2026, 1, 1));
        dto.setEmploymentType("CLT");
        dto.setActive(true);
        dto.setPositionId(1L);
        dto.setDepartmentId(1L);
    }
}
