package com.locadora_rdt_backend.tests.modules.receivables.controller;

import com.locadora_rdt_backend.modules.receivables.controller.ReceivableFileController;
import com.locadora_rdt_backend.modules.receivables.dto.ReceivableFileDTO;
import com.locadora_rdt_backend.modules.receivables.dto.ReceivableFileViewDTO;
import com.locadora_rdt_backend.modules.receivables.service.ReceivableFileService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReceivableFileController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReceivableFileControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReceivableFileService service;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void uploadShouldReturnCreated() throws Exception {
        ReceivableFileDTO dto = createFileDTO();
        Mockito.when(service.upload(eq(1L), eq("Comprovante"), any())).thenReturn(dto);

        mockMvc.perform(multipart("/receivables/{receivableId}/files", 1L)
                        .file(new MockMultipartFile("file", "comprovante.pdf", "application/pdf", new byte[]{1}))
                        .param("name", "Comprovante"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(2L));
    }

    @Test
    void findAllByReceivableShouldReturnList() throws Exception {
        Mockito.when(service.findAllByReceivable(1L)).thenReturn(List.of(createFileDTO()));

        mockMvc.perform(get("/receivables/{receivableId}/files", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L));
    }

    @Test
    void viewShouldReturnInlineFile() throws Exception {
        Mockito.when(service.download(1L, 2L)).thenReturn(createViewDTO());

        mockMvc.perform(get("/receivables/{receivableId}/files/{fileId}/view", 1L, 2L))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "inline; filename=\"arquivo.txt\""))
                .andExpect(content().bytes(new byte[]{1}));
    }

    @Test
    void downloadShouldReturnAttachmentFile() throws Exception {
        Mockito.when(service.download(1L, 2L)).thenReturn(createViewDTO());

        mockMvc.perform(get("/receivables/{receivableId}/files/{fileId}/download", 1L, 2L))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"arquivo.txt\""));
    }

    @Test
    void deleteShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/receivables/{receivableId}/files/{fileId}", 1L, 2L))
                .andExpect(status().isNoContent());

        Mockito.verify(service).delete(1L, 2L);
    }

    private ReceivableFileDTO createFileDTO() {
        ReceivableFileDTO dto = new ReceivableFileDTO();
        dto.setId(2L);
        dto.setName("Comprovante");
        dto.setReceivableId(1L);
        return dto;
    }

    private ReceivableFileViewDTO createViewDTO() {
        return new ReceivableFileViewDTO("arquivo.txt", MediaType.TEXT_PLAIN_VALUE, new byte[]{1});
    }
}
