package com.locadora_rdt_backend.tests.modules.suppliers.service;

import com.locadora_rdt_backend.common.exception.FileException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.suppliers.dto.SupplierFileDTO;
import com.locadora_rdt_backend.modules.suppliers.dto.SupplierFileViewDTO;
import com.locadora_rdt_backend.modules.suppliers.model.Supplier;
import com.locadora_rdt_backend.modules.suppliers.model.SupplierFile;
import com.locadora_rdt_backend.modules.suppliers.repository.SupplierFileRepository;
import com.locadora_rdt_backend.modules.suppliers.repository.SupplierRepository;
import com.locadora_rdt_backend.modules.suppliers.service.SupplierFileServiceImpl;
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
class SupplierFileServiceTests {

    @Mock
    private SupplierFileRepository fileRepository;

    @Mock
    private SupplierRepository supplierRepository;

    private SupplierFileServiceImpl service;
    private Supplier supplier;
    private SupplierFile supplierFile;

    @BeforeEach
    void setUp() {
        service = new SupplierFileServiceImpl(fileRepository, supplierRepository);
        supplier = new Supplier();
        supplier.setId(1L);

        supplierFile = new SupplierFile();
        supplierFile.setId(10L);
        supplierFile.setName("Contrato");
        supplierFile.setOriginalFileName("contrato.pdf");
        supplierFile.setStoredFileName("uuid-contrato.pdf");
        supplierFile.setContentType("application/pdf");
        supplierFile.setSize(4L);
        supplierFile.setData(new byte[]{1, 2, 3, 4});
        supplierFile.setSupplier(supplier);
    }

    @Test
    void uploadShouldSaveNormalizedFile() {
        MultipartFile file = pdfFile("contrato á.pdf");
        Mockito.when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        Mockito.when(fileRepository.save(Mockito.any(SupplierFile.class))).thenAnswer(invocation -> {
            SupplierFile entity = invocation.getArgument(0);
            entity.setId(10L);
            return entity;
        });

        SupplierFileDTO dto = service.upload(1L, " Contrato ", file);

        ArgumentCaptor<SupplierFile> captor = ArgumentCaptor.forClass(SupplierFile.class);
        Mockito.verify(fileRepository).save(captor.capture());
        SupplierFile saved = captor.getValue();

        Assertions.assertEquals(10L, dto.getId());
        Assertions.assertSame(supplier, saved.getSupplier());
        Assertions.assertEquals("Contrato", saved.getName());
        Assertions.assertTrue(saved.getStoredFileName().endsWith("-contrato_a.pdf"));
        Assertions.assertArrayEquals(new byte[]{1, 2, 3}, saved.getData());
    }

    @Test
    void findAllBySupplierShouldReturnFiles() {
        Mockito.when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        Mockito.when(fileRepository.findBySupplierIdOrderByIdDesc(1L)).thenReturn(List.of(supplierFile));

        List<SupplierFileDTO> result = service.findAllBySupplier(1L);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Contrato", result.get(0).getName());
    }

    @Test
    void downloadShouldReturnFileViewWhenFileBelongsToSupplier() {
        Mockito.when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        Mockito.when(fileRepository.findById(10L)).thenReturn(Optional.of(supplierFile));

        SupplierFileViewDTO dto = service.download(1L, 10L);

        Assertions.assertEquals("contrato.pdf", dto.getFileName());
        Assertions.assertEquals("application/pdf", dto.getContentType());
        Assertions.assertArrayEquals(new byte[]{1, 2, 3, 4}, dto.getData());
    }

    @Test
    void deleteShouldRemoveFileWhenFileBelongsToSupplier() {
        Mockito.when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        Mockito.when(fileRepository.findById(10L)).thenReturn(Optional.of(supplierFile));

        service.delete(1L, 10L);

        Mockito.verify(fileRepository).delete(supplierFile);
    }

    @Test
    void operationsShouldThrowWhenSupplierDoesNotExist() {
        Mockito.when(supplierRepository.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findAllBySupplier(999L));
    }

    @Test
    void downloadShouldThrowWhenFileDoesNotExistOrBelongsToAnotherSupplier() {
        Mockito.when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        Mockito.when(fileRepository.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.download(1L, 999L));

        Supplier otherSupplier = new Supplier();
        otherSupplier.setId(2L);
        supplierFile.setSupplier(otherSupplier);
        Mockito.when(fileRepository.findById(10L)).thenReturn(Optional.of(supplierFile));

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.download(1L, 10L));
    }

    @Test
    void uploadShouldValidateNameAndFile() {
        Mockito.when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        MultipartFile validFile = pdfFile("file.pdf");
        MultipartFile emptyFile = emptyPdfFile("empty.pdf");
        MultipartFile invalidExtensionFile = file("file.exe", "application/octet-stream", new byte[]{1});

        Assertions.assertThrows(FileException.class, () -> service.upload(1L, null, validFile));
        Assertions.assertThrows(FileException.class, () -> service.upload(1L, " ", validFile));
        Assertions.assertThrows(FileException.class, () -> service.upload(1L, "Contrato", null));
        Assertions.assertThrows(FileException.class, () -> service.upload(1L, "Contrato", emptyFile));
        Assertions.assertThrows(FileException.class, () -> service.upload(1L, "Contrato", invalidExtensionFile));
    }

    @Test
    void uploadShouldThrowWhenFileCannotBeRead() throws IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        Mockito.when(file.isEmpty()).thenReturn(false);
        Mockito.when(file.getOriginalFilename()).thenReturn("contrato.pdf");
        Mockito.when(file.getSize()).thenReturn(1L);
        Mockito.when(file.getContentType()).thenReturn("application/pdf");
        Mockito.when(file.getBytes()).thenThrow(new IOException("read error"));

        Assertions.assertThrows(FileException.class, () -> service.upload(1L, "Contrato", file));
    }
}
