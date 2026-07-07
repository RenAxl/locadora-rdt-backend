package com.locadora_rdt_backend.tests.modules.financial.receivables.service;

import com.locadora_rdt_backend.common.exception.FileException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableFileDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableFileViewDTO;
import com.locadora_rdt_backend.modules.financial.receivables.model.Receivable;
import com.locadora_rdt_backend.modules.financial.receivables.model.ReceivableFile;
import com.locadora_rdt_backend.modules.financial.receivables.repository.ReceivableFileRepository;
import com.locadora_rdt_backend.modules.financial.receivables.repository.ReceivableRepository;
import com.locadora_rdt_backend.modules.financial.receivables.service.ReceivableFileServiceImpl;
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
class ReceivableFileServiceTests {

    @Mock
    private ReceivableFileRepository fileRepository;

    @Mock
    private ReceivableRepository receivableRepository;

    private ReceivableFileServiceImpl service;
    private Receivable receivable;
    private ReceivableFile receivableFile;

    @BeforeEach
    void setUp() {
        service = new ReceivableFileServiceImpl(fileRepository, receivableRepository);
        receivable = new Receivable();
        receivable.setId(1L);

        receivableFile = new ReceivableFile();
        receivableFile.setId(10L);
        receivableFile.setName("Comprovante");
        receivableFile.setOriginalFileName("comprovante.pdf");
        receivableFile.setStoredFileName("uuid-comprovante.pdf");
        receivableFile.setContentType("application/pdf");
        receivableFile.setSize(4L);
        receivableFile.setData(new byte[]{1, 2, 3, 4});
        receivableFile.setReceivable(receivable);
    }

    @Test
    void uploadShouldSaveNormalizedFileAndUpdateReceivableFileName() {
        MultipartFile file = pdfFile("comprovante á.pdf");
        Mockito.when(receivableRepository.findById(1L)).thenReturn(Optional.of(receivable));
        Mockito.when(fileRepository.save(Mockito.any(ReceivableFile.class))).thenAnswer(invocation -> {
            ReceivableFile entity = invocation.getArgument(0);
            entity.setId(10L);
            return entity;
        });

        ReceivableFileDTO dto = service.upload(1L, " Comprovante ", file);

        ArgumentCaptor<ReceivableFile> captor = ArgumentCaptor.forClass(ReceivableFile.class);
        Mockito.verify(fileRepository).save(captor.capture());
        ReceivableFile saved = captor.getValue();

        Assertions.assertEquals(10L, dto.getId());
        Assertions.assertSame(receivable, saved.getReceivable());
        Assertions.assertEquals("Comprovante", saved.getName());
        Assertions.assertEquals("comprovante á.pdf", saved.getOriginalFileName());
        Assertions.assertTrue(saved.getStoredFileName().endsWith("-comprovante_a.pdf"));
        Assertions.assertEquals("comprovante á.pdf", receivable.getFileName());
    }

    @Test
    void findAllByReceivableShouldReturnFiles() {
        Mockito.when(receivableRepository.findById(1L)).thenReturn(Optional.of(receivable));
        Mockito.when(fileRepository.findByReceivableIdOrderByIdDesc(1L)).thenReturn(List.of(receivableFile));

        List<ReceivableFileDTO> result = service.findAllByReceivable(1L);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Comprovante", result.get(0).getName());
    }

    @Test
    void downloadShouldReturnFileViewWhenFileBelongsToReceivable() {
        Mockito.when(receivableRepository.findById(1L)).thenReturn(Optional.of(receivable));
        Mockito.when(fileRepository.findById(10L)).thenReturn(Optional.of(receivableFile));

        ReceivableFileViewDTO dto = service.download(1L, 10L);

        Assertions.assertEquals("comprovante.pdf", dto.getFileName());
        Assertions.assertEquals("application/pdf", dto.getContentType());
        Assertions.assertArrayEquals(new byte[]{1, 2, 3, 4}, dto.getData());
    }

    @Test
    void deleteShouldRemoveFileWhenFileBelongsToReceivable() {
        Mockito.when(receivableRepository.findById(1L)).thenReturn(Optional.of(receivable));
        Mockito.when(fileRepository.findById(10L)).thenReturn(Optional.of(receivableFile));

        service.delete(1L, 10L);

        Mockito.verify(fileRepository).delete(receivableFile);
    }

    @Test
    void shouldThrowWhenReceivableDoesNotExist() {
        Mockito.when(receivableRepository.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findAllByReceivable(999L));
    }

    @Test
    void shouldThrowWhenFileDoesNotExistOrBelongsToAnotherReceivable() {
        Mockito.when(receivableRepository.findById(1L)).thenReturn(Optional.of(receivable));
        Mockito.when(fileRepository.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.download(1L, 999L));

        Receivable other = new Receivable();
        other.setId(2L);
        receivableFile.setReceivable(other);
        Mockito.when(fileRepository.findById(10L)).thenReturn(Optional.of(receivableFile));

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.download(1L, 10L));
    }

    @Test
    void uploadShouldValidateFile() {
        Mockito.when(receivableRepository.findById(1L)).thenReturn(Optional.of(receivable));
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
        Mockito.when(receivableRepository.findById(1L)).thenReturn(Optional.of(receivable));
        Mockito.when(file.isEmpty()).thenReturn(false);
        Mockito.when(file.getOriginalFilename()).thenReturn("comprovante.pdf");
        Mockito.when(file.getSize()).thenReturn(1L);
        Mockito.when(file.getContentType()).thenReturn("application/pdf");
        Mockito.when(file.getBytes()).thenThrow(new IOException("read error"));

        Assertions.assertThrows(FileException.class, () -> service.upload(1L, "Comprovante", file));
    }
}
