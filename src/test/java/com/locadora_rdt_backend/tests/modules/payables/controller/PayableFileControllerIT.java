package com.locadora_rdt_backend.tests.modules.payables.controller;

import com.locadora_rdt_backend.modules.financial.payables.controller.PayableFileController;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableFileDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableFileViewDTO;
import com.locadora_rdt_backend.modules.financial.payables.service.PayableFileService;
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

@WebMvcTest(PayableFileController.class)
@AutoConfigureMockMvc(addFilters = false)
class PayableFileControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PayableFileService service;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void uploadShouldReturnCreated() throws Exception {
        PayableFileDTO dto = createFileDTO();
        Mockito.when(service.upload(eq(1L), eq("Comprovante"), any())).thenReturn(dto);

        mockMvc.perform(multipart("/payables/{payableId}/files", 1L)
                        .file(new MockMultipartFile("file", "comprovante.pdf", "application/pdf", new byte[]{1}))
                        .param("name", "Comprovante"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(2L));
    }

    @Test
    void findAllByPayableShouldReturnList() throws Exception {
        Mockito.when(service.findAllByPayable(1L)).thenReturn(List.of(createFileDTO()));

        mockMvc.perform(get("/payables/{payableId}/files", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L));
    }

    @Test
    void viewShouldReturnInlineFile() throws Exception {
        Mockito.when(service.download(1L, 2L)).thenReturn(createViewDTO());

        mockMvc.perform(get("/payables/{payableId}/files/{fileId}/view", 1L, 2L))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "inline; filename=\"arquivo.txt\""))
                .andExpect(content().bytes(new byte[]{1}));
    }

    @Test
    void downloadShouldReturnAttachmentFile() throws Exception {
        Mockito.when(service.download(1L, 2L)).thenReturn(createViewDTO());

        mockMvc.perform(get("/payables/{payableId}/files/{fileId}/download", 1L, 2L))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"arquivo.txt\""));
    }

    @Test
    void deleteShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/payables/{payableId}/files/{fileId}", 1L, 2L))
                .andExpect(status().isNoContent());

        Mockito.verify(service).delete(1L, 2L);
    }

    private PayableFileDTO createFileDTO() {
        PayableFileDTO dto = new PayableFileDTO();
        dto.setId(2L);
        dto.setName("Comprovante");
        dto.setPayableId(1L);
        return dto;
    }

    private PayableFileViewDTO createViewDTO() {
        return new PayableFileViewDTO("arquivo.txt", MediaType.TEXT_PLAIN_VALUE, new byte[]{1});
    }
}
