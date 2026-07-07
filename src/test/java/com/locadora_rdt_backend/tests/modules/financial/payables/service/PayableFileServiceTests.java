package com.locadora_rdt_backend.tests.modules.financial.payables.service;

import com.locadora_rdt_backend.common.exception.FileException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableFileDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableFileViewDTO;
import com.locadora_rdt_backend.modules.financial.payables.model.Payable;
import com.locadora_rdt_backend.modules.financial.payables.model.PayableFile;
import com.locadora_rdt_backend.modules.financial.payables.repository.PayableFileRepository;
import com.locadora_rdt_backend.modules.financial.payables.repository.PayableRepository;
import com.locadora_rdt_backend.modules.financial.payables.service.PayableFileServiceImpl;
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
class PayableFileServiceTests {

    @Mock
    private PayableFileRepository fileRepository;

    @Mock
    private PayableRepository payableRepository;

    private PayableFileServiceImpl service;
    private Payable payable;
    private PayableFile payableFile;

    @BeforeEach
    void setUp() {
        service = new PayableFileServiceImpl(fileRepository, payableRepository);
        payable = new Payable();
        payable.setId(1L);

        payableFile = new PayableFile();
        payableFile.setId(10L);
        payableFile.setName("Comprovante");
        payableFile.setOriginalFileName("comprovante.pdf");
        payableFile.setStoredFileName("uuid-comprovante.pdf");
        payableFile.setContentType("application/pdf");
        payableFile.setSize(4L);
        payableFile.setData(new byte[]{1, 2, 3, 4});
        payableFile.setPayable(payable);
    }

    @Test
    void uploadShouldSaveNormalizedFileAndUpdatePayableFileName() {
        MultipartFile file = pdfFile("comprovante á.pdf");
        Mockito.when(payableRepository.findById(1L)).thenReturn(Optional.of(payable));
        Mockito.when(fileRepository.save(Mockito.any(PayableFile.class))).thenAnswer(invocation -> {
            PayableFile entity = invocation.getArgument(0);
            entity.setId(10L);
            return entity;
        });

        PayableFileDTO dto = service.upload(1L, " Comprovante ", file);

        ArgumentCaptor<PayableFile> captor = ArgumentCaptor.forClass(PayableFile.class);
        Mockito.verify(fileRepository).save(captor.capture());
        PayableFile saved = captor.getValue();

        Assertions.assertEquals(10L, dto.getId());
        Assertions.assertSame(payable, saved.getPayable());
        Assertions.assertEquals("Comprovante", saved.getName());
        Assertions.assertEquals("comprovante á.pdf", saved.getOriginalFileName());
        Assertions.assertTrue(saved.getStoredFileName().endsWith("-comprovante_a.pdf"));
        Assertions.assertEquals("comprovante á.pdf", payable.getFileName());
    }

    @Test
    void findAllByPayableShouldReturnFiles() {
        Mockito.when(payableRepository.findById(1L)).thenReturn(Optional.of(payable));
        Mockito.when(fileRepository.findByPayableIdOrderByIdDesc(1L)).thenReturn(List.of(payableFile));

        List<PayableFileDTO> result = service.findAllByPayable(1L);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Comprovante", result.get(0).getName());
    }

    @Test
    void downloadShouldReturnFileViewWhenFileBelongsToPayable() {
        Mockito.when(payableRepository.findById(1L)).thenReturn(Optional.of(payable));
        Mockito.when(fileRepository.findById(10L)).thenReturn(Optional.of(payableFile));

        PayableFileViewDTO dto = service.download(1L, 10L);

        Assertions.assertEquals("comprovante.pdf", dto.getFileName());
        Assertions.assertEquals("application/pdf", dto.getContentType());
        Assertions.assertArrayEquals(new byte[]{1, 2, 3, 4}, dto.getData());
    }

    @Test
    void deleteShouldRemoveFileWhenFileBelongsToPayable() {
        Mockito.when(payableRepository.findById(1L)).thenReturn(Optional.of(payable));
        Mockito.when(fileRepository.findById(10L)).thenReturn(Optional.of(payableFile));

        service.delete(1L, 10L);

        Mockito.verify(fileRepository).delete(payableFile);
    }

    @Test
    void shouldThrowWhenPayableDoesNotExist() {
        Mockito.when(payableRepository.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findAllByPayable(999L));
    }

    @Test
    void shouldThrowWhenFileDoesNotExistOrBelongsToAnotherPayable() {
        Mockito.when(payableRepository.findById(1L)).thenReturn(Optional.of(payable));
        Mockito.when(fileRepository.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.download(1L, 999L));

        Payable other = new Payable();
        other.setId(2L);
        payableFile.setPayable(other);
        Mockito.when(fileRepository.findById(10L)).thenReturn(Optional.of(payableFile));

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.download(1L, 10L));
    }

    @Test
    void uploadShouldValidateFile() {
        Mockito.when(payableRepository.findById(1L)).thenReturn(Optional.of(payable));
        MultipartFile emptyFile = emptyPdfFile("empty.pdf");
        MultipartFile blankNameFile = file(" ", "application/pdf", new byte[]{1});
        MultipartFile invalidExtensionFile = file("file.exe", "application/octet-stream", new byte[]{1});

        Assertions.assertThrows(FileException.class, () -> service.upload(1L, "Comprovante", null));
        Assertions.assertThrows(FileException.class, () -> service.upload(1L, "Comprovante", emptyFile));
        Assertions.assertThrows(FileException.class, () -> service.upload(1L, "Comprovante", blankNameFile));
        Assertions.assertThrows(FileException.class, () -> service.upload(1L, "Comprovante", invalidExtensionFile));
        Assertions.assertThrows(FileException.class, () -> service.upload(1L, " ", pdfFile("comprovante.pdf")));
    }

    @Test
    void uploadShouldThrowWhenFileCannotBeRead() throws IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(payableRepository.findById(1L)).thenReturn(Optional.of(payable));
        Mockito.when(file.isEmpty()).thenReturn(false);
        Mockito.when(file.getOriginalFilename()).thenReturn("comprovante.pdf");
        Mockito.when(file.getSize()).thenReturn(1L);
        Mockito.when(file.getContentType()).thenReturn("application/pdf");
        Mockito.when(file.getBytes()).thenThrow(new IOException("read error"));

        Assertions.assertThrows(FileException.class, () -> service.upload(1L, "Comprovante", file));
    }
}
