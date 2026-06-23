package com.locadora_rdt_backend.tests.modules.employees.service;

import com.locadora_rdt_backend.common.exception.FileException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeFileDTO;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeFileViewDTO;
import com.locadora_rdt_backend.modules.employees.model.Employee;
import com.locadora_rdt_backend.modules.employees.model.EmployeeFile;
import com.locadora_rdt_backend.modules.employees.repository.EmployeeFileRepository;
import com.locadora_rdt_backend.modules.employees.repository.EmployeeRepository;
import com.locadora_rdt_backend.modules.employees.service.EmployeeFileServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class EmployeeFileServiceTests {

    @Mock
    private EmployeeFileRepository fileRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    private EmployeeFileServiceImpl service;
    private Employee employee;
    private EmployeeFile employeeFile;

    @BeforeEach
    void setUp() {
        service = new EmployeeFileServiceImpl(fileRepository, employeeRepository);
        employee = new Employee();
        employee.setId(1L);

        employeeFile = new EmployeeFile();
        employeeFile.setId(10L);
        employeeFile.setName("Contrato");
        employeeFile.setOriginalFileName("contrato.pdf");
        employeeFile.setStoredFileName("uuid-contrato.pdf");
        employeeFile.setContentType("application/pdf");
        employeeFile.setSize(4L);
        employeeFile.setData(new byte[]{1, 2, 3, 4});
        employeeFile.setEmployee(employee);
    }

    @Test
    void uploadShouldSaveNormalizedFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "contrato á.pdf",
                "application/pdf",
                new byte[]{1, 2, 3}
        );
        Mockito.when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        Mockito.when(fileRepository.save(Mockito.any(EmployeeFile.class))).thenAnswer(invocation -> {
            EmployeeFile entity = invocation.getArgument(0);
            entity.setId(10L);
            return entity;
        });

        EmployeeFileDTO dto = service.upload(1L, " Contrato ", file);

        ArgumentCaptor<EmployeeFile> captor = ArgumentCaptor.forClass(EmployeeFile.class);
        Mockito.verify(fileRepository).save(captor.capture());
        EmployeeFile saved = captor.getValue();

        Assertions.assertEquals(10L, dto.getId());
        Assertions.assertSame(employee, saved.getEmployee());
        Assertions.assertEquals("Contrato", saved.getName());
        Assertions.assertTrue(saved.getStoredFileName().endsWith("-contrato_a.pdf"));
        Assertions.assertArrayEquals(new byte[]{1, 2, 3}, saved.getData());
    }

    @Test
    void findAllByEmployeeShouldReturnFiles() {
        Mockito.when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        Mockito.when(fileRepository.findByEmployeeIdOrderByIdDesc(1L)).thenReturn(List.of(employeeFile));

        List<EmployeeFileDTO> result = service.findAllByEmployee(1L);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Contrato", result.get(0).getName());
    }

    @Test
    void downloadShouldReturnFileViewWhenFileBelongsToEmployee() {
        Mockito.when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        Mockito.when(fileRepository.findById(10L)).thenReturn(Optional.of(employeeFile));

        EmployeeFileViewDTO dto = service.download(1L, 10L);

        Assertions.assertEquals("contrato.pdf", dto.getFileName());
        Assertions.assertEquals("application/pdf", dto.getContentType());
        Assertions.assertArrayEquals(new byte[]{1, 2, 3, 4}, dto.getData());
    }

    @Test
    void deleteShouldRemoveFileWhenFileBelongsToEmployee() {
        Mockito.when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        Mockito.when(fileRepository.findById(10L)).thenReturn(Optional.of(employeeFile));

        service.delete(1L, 10L);

        Mockito.verify(fileRepository).delete(employeeFile);
    }

    @Test
    void findEmployeeByIdShouldThrowWhenEmployeeDoesNotExist() {
        Mockito.when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findEmployeeById(999L));
    }

    @Test
    void findFileBelongsToEmployeeShouldThrowWhenFileDoesNotExistOrBelongsToAnotherEmployee() {
        Mockito.when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        Mockito.when(fileRepository.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findFileBelongsToEmployee(1L, 999L));

        Employee otherEmployee = new Employee();
        otherEmployee.setId(2L);
        employeeFile.setEmployee(otherEmployee);
        Mockito.when(fileRepository.findById(10L)).thenReturn(Optional.of(employeeFile));

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findFileBelongsToEmployee(1L, 10L));
    }

    @Test
    void uploadShouldValidateFile() {
        Mockito.when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        Assertions.assertThrows(FileException.class, () -> service.upload(1L, "Contrato", null));
        Assertions.assertThrows(FileException.class, () -> service.upload(1L, "Contrato",
                new MockMultipartFile("file", "empty.pdf", "application/pdf", new byte[]{})));
        Assertions.assertThrows(FileException.class, () -> service.upload(1L, "Contrato",
                new MockMultipartFile("file", "", "application/pdf", new byte[]{1})));
        Assertions.assertThrows(FileException.class, () -> service.upload(1L, "Contrato",
                new MockMultipartFile("file", "file.exe", null, new byte[]{1})));
    }

    @Test
    void uploadShouldThrowWhenFileCannotBeRead() throws IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        Mockito.when(file.isEmpty()).thenReturn(false);
        Mockito.when(file.getOriginalFilename()).thenReturn("contrato.pdf");
        Mockito.when(file.getSize()).thenReturn(1L);
        Mockito.when(file.getContentType()).thenReturn("application/pdf");
        Mockito.when(file.getBytes()).thenThrow(new IOException("read error"));

        Assertions.assertThrows(FileException.class, () -> service.upload(1L, "Contrato", file));
    }
}
