package com.locadora_rdt_backend.tests.modules.customers.service;

import com.locadora_rdt_backend.common.exception.FileException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.customers.dto.file.CustomerFileDTO;
import com.locadora_rdt_backend.modules.customers.model.Customer;
import com.locadora_rdt_backend.modules.customers.model.CustomerFile;
import com.locadora_rdt_backend.modules.customers.repository.CustomerFileRepository;
import com.locadora_rdt_backend.modules.customers.repository.CustomerRepository;
import com.locadora_rdt_backend.modules.customers.service.CustomerFileService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class CustomerFileServiceTests {

    @InjectMocks
    private CustomerFileService service;

    @Mock
    private CustomerFileRepository fileRepository;

    @Mock
    private CustomerRepository customerRepository;

    private Long existingCustomerId;
    private Long nonExistingCustomerId;
    private Long existingFileId;
    private Long nonExistingFileId;

    private Customer customer;
    private CustomerFile customerFile;

    @BeforeEach
    void setUp() {

        existingCustomerId = 1L;
        nonExistingCustomerId = 999L;
        existingFileId = 10L;
        nonExistingFileId = 999L;

        customer = new Customer();
        customer.setId(existingCustomerId);

        customerFile = new CustomerFile();
        customerFile.setId(existingFileId);
        customerFile.setCustomer(customer);
        customerFile.setName("Contrato");
        customerFile.setOriginalFileName("file.pdf");
        customerFile.setContentType("application/pdf");
        customerFile.setData("data".getBytes());

        Mockito.when(customerRepository.findById(existingCustomerId))
                .thenReturn(Optional.of(customer));

        Mockito.when(customerRepository.findById(nonExistingCustomerId))
                .thenReturn(Optional.empty());

        Mockito.when(fileRepository.findById(existingFileId))
                .thenReturn(Optional.of(customerFile));

        Mockito.when(fileRepository.findById(nonExistingFileId))
                .thenReturn(Optional.empty());

        Mockito.when(fileRepository.save(Mockito.any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }


    @Test
    public void uploadShouldSaveFileWhenValidData() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "file.pdf", "application/pdf", "content".getBytes()
        );

        CustomerFileDTO result = service.upload(existingCustomerId, "Contrato", file);

        Assertions.assertNotNull(result);
        Mockito.verify(fileRepository).save(Mockito.any());
    }

    @Test
    public void uploadShouldThrowExceptionWhenCustomerNotFound() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "file.pdf", "application/pdf", "content".getBytes()
        );

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.upload(nonExistingCustomerId, "Contrato", file);
        });
    }

    @Test
    public void uploadShouldThrowExceptionWhenFileIsEmpty() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "file.pdf", "application/pdf", new byte[]{}
        );

        Assertions.assertThrows(FileException.class, () -> {
            service.upload(existingCustomerId, "Contrato", file);
        });
    }


    @Test
    public void findAllShouldReturnListWhenFilesExist() {
        Mockito.when(fileRepository.findByCustomerIdOrderByIdDesc(existingCustomerId))
                .thenReturn(List.of(customerFile));

        List<CustomerFileDTO> result = service.findAllByCustomer(existingCustomerId);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.size());
    }

    @Test
    public void findAllShouldReturnEmptyListWhenNoFiles() {
        Mockito.when(fileRepository.findByCustomerIdOrderByIdDesc(existingCustomerId))
                .thenReturn(List.of());

        List<CustomerFileDTO> result = service.findAllByCustomer(existingCustomerId);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void findAllShouldThrowExceptionWhenCustomerNotFound() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findAllByCustomer(nonExistingCustomerId);
        });
    }



    @Test
    public void deleteShouldDeleteWhenExists() {
        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingCustomerId, existingFileId);
        });

        Mockito.verify(fileRepository).delete(Mockito.any());
    }

    @Test
    public void deleteShouldThrowExceptionWhenFileNotFound() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(existingCustomerId, nonExistingFileId);
        });
    }

    @Test
    public void deleteShouldThrowExceptionWhenCustomerNotFound() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingCustomerId, existingFileId);
        });
    }
}