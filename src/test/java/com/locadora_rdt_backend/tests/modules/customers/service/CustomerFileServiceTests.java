package com.locadora_rdt_backend.tests.modules.customers.service;

import com.locadora_rdt_backend.common.exception.FileException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.customers.dto.CustomerFileDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerFileViewDTO;
import com.locadora_rdt_backend.modules.customers.model.Customer;
import com.locadora_rdt_backend.modules.customers.model.CustomerFile;
import com.locadora_rdt_backend.modules.customers.repository.CustomerFileRepository;
import com.locadora_rdt_backend.modules.customers.repository.CustomerRepository;
import com.locadora_rdt_backend.modules.customers.service.CustomerFileServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.locadora_rdt_backend.tests.common.TestFileFactory.emptyPdfFile;
import static com.locadora_rdt_backend.tests.common.TestFileFactory.file;
import static com.locadora_rdt_backend.tests.common.TestFileFactory.pdfFile;

@ExtendWith(MockitoExtension.class)
class CustomerFileServiceTests {

    @Mock
    private CustomerFileRepository fileRepository;

    @Mock
    private CustomerRepository customerRepository;

    private CustomerFileServiceImpl service;
    private Customer customer;
    private CustomerFile customerFile;

    @BeforeEach
    void setUp() {
        service = new CustomerFileServiceImpl(fileRepository, customerRepository);
        customer = new Customer();
        customer.setId(1L);

        customerFile = new CustomerFile();
        customerFile.setId(10L);
        customerFile.setName("Contrato");
        customerFile.setOriginalFileName("contrato.pdf");
        customerFile.setStoredFileName("uuid-contrato.pdf");
        customerFile.setContentType("application/pdf");
        customerFile.setSize(4L);
        customerFile.setData(new byte[]{1, 2, 3, 4});
        customerFile.setCustomer(customer);
    }

    @Test
    void uploadShouldSaveNormalizedFile() {
        MultipartFile file = pdfFile("contrato á.pdf");
        Mockito.when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        Mockito.when(fileRepository.save(Mockito.any(CustomerFile.class))).thenAnswer(invocation -> {
            CustomerFile entity = invocation.getArgument(0);
            entity.setId(10L);
            return entity;
        });

        CustomerFileDTO dto = service.upload(1L, " Contrato ", file);

        ArgumentCaptor<CustomerFile> captor = ArgumentCaptor.forClass(CustomerFile.class);
        Mockito.verify(fileRepository).save(captor.capture());
        CustomerFile saved = captor.getValue();

        Assertions.assertEquals(10L, dto.getId());
        Assertions.assertSame(customer, saved.getCustomer());
        Assertions.assertEquals("Contrato", saved.getName());
        Assertions.assertEquals("contrato á.pdf", saved.getOriginalFileName());
        Assertions.assertTrue(saved.getStoredFileName().endsWith("-contrato_a.pdf"));
        Assertions.assertEquals("application/pdf", saved.getContentType());
        Assertions.assertArrayEquals(new byte[]{1, 2, 3}, saved.getData());
    }

    @Test
    void findAllByCustomerShouldReturnFiles() {
        Mockito.when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        Mockito.when(fileRepository.findByCustomerIdOrderByIdDesc(1L)).thenReturn(List.of(customerFile));

        List<CustomerFileDTO> result = service.findAllByCustomer(1L);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Contrato", result.get(0).getName());
    }

    @Test
    void downloadShouldReturnFileViewWhenFileBelongsToCustomer() {
        Mockito.when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        Mockito.when(fileRepository.findById(10L)).thenReturn(Optional.of(customerFile));

        CustomerFileViewDTO dto = service.download(1L, 10L);

        Assertions.assertEquals("contrato.pdf", dto.getFileName());
        Assertions.assertEquals("application/pdf", dto.getContentType());
        Assertions.assertArrayEquals(new byte[]{1, 2, 3, 4}, dto.getData());
    }

    @Test
    void deleteShouldRemoveFileWhenFileBelongsToCustomer() {
        Mockito.when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        Mockito.when(fileRepository.findById(10L)).thenReturn(Optional.of(customerFile));

        service.delete(1L, 10L);

        Mockito.verify(fileRepository).delete(customerFile);
    }

    @Test
    void findCustomerByIdShouldThrowWhenCustomerDoesNotExist() {
        Mockito.when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findCustomerById(999L));
    }

    @Test
    void findFileBelongsToCustomerShouldThrowWhenFileDoesNotExistOrBelongsToAnotherCustomer() {
        Mockito.when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        Mockito.when(fileRepository.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findFileBelongsToCustomer(1L, 999L));

        Customer otherCustomer = new Customer();
        otherCustomer.setId(2L);
        customerFile.setCustomer(otherCustomer);
        Mockito.when(fileRepository.findById(10L)).thenReturn(Optional.of(customerFile));

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findFileBelongsToCustomer(1L, 10L));
    }

    @Test
    void uploadShouldValidateFile() {
        Mockito.when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        MultipartFile emptyFile = emptyPdfFile("empty.pdf");
        MultipartFile blankNameFile = file(" ", "application/pdf", new byte[]{1});
        MultipartFile invalidExtensionFile = file("file.exe", "application/octet-stream", new byte[]{1});

        Assertions.assertThrows(FileException.class, () -> service.upload(1L, "Contrato", null));
        Assertions.assertThrows(FileException.class, () -> service.upload(1L, "Contrato", emptyFile));
        Assertions.assertThrows(FileException.class, () -> service.upload(1L, "Contrato", blankNameFile));
        Assertions.assertThrows(FileException.class, () -> service.upload(1L, "Contrato", invalidExtensionFile));
    }

    @Test
    void uploadShouldThrowWhenFileCannotBeRead() throws IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        Mockito.when(file.isEmpty()).thenReturn(false);
        Mockito.when(file.getOriginalFilename()).thenReturn("contrato.pdf");
        Mockito.when(file.getSize()).thenReturn(1L);
        Mockito.when(file.getContentType()).thenReturn("application/pdf");
        Mockito.when(file.getBytes()).thenThrow(new IOException("read error"));

        Assertions.assertThrows(FileException.class, () -> service.upload(1L, "Contrato", file));
    }
}
