package com.locadora_rdt_backend.tests.modules.suppliers.controller;

import com.locadora_rdt_backend.modules.suppliers.controller.SupplierFileController;
import com.locadora_rdt_backend.modules.suppliers.dto.SupplierFileDTO;
import com.locadora_rdt_backend.modules.suppliers.dto.SupplierFileViewDTO;
import com.locadora_rdt_backend.modules.suppliers.service.SupplierFileService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SupplierFileController.class)
@AutoConfigureMockMvc(addFilters = false)
public class SupplierFileControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SupplierFileService service;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void uploadShouldReturnCreated() throws Exception {
        Mockito.when(service.upload(eq(1L), eq("Arquivo"), any())).thenReturn(createDTO());

        mockMvc.perform(multipart("/suppliers/{supplierId}/files", 1L)
                        .file(new MockMultipartFile("file", "arquivo.txt", "text/plain", new byte[]{1}))
                        .param("name", "Arquivo"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L));
    }

    @Test
    void findAllBySupplierShouldReturnList() throws Exception {
        Mockito.when(service.findAllBySupplier(1L)).thenReturn(List.of(createDTO()));

        mockMvc.perform(get("/suppliers/{supplierId}/files", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L));
    }

    @Test
    void viewShouldReturnInlineFile() throws Exception {
        Mockito.when(service.download(1L, 2L)).thenReturn(createViewDTO());

        mockMvc.perform(get("/suppliers/{supplierId}/files/{fileId}/view", 1L, 2L))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "inline; filename=\"arquivo.txt\""));
    }

    @Test
    void downloadShouldReturnAttachmentFile() throws Exception {
        Mockito.when(service.download(1L, 2L)).thenReturn(createViewDTO());

        mockMvc.perform(get("/suppliers/{supplierId}/files/{fileId}/download", 1L, 2L))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"arquivo.txt\""));
    }

    @Test
    void deleteShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/suppliers/{supplierId}/files/{fileId}", 1L, 2L))
                .andExpect(status().isNoContent());
    }

    private SupplierFileDTO createDTO() {
        SupplierFileDTO dto = new SupplierFileDTO();
        dto.setId(2L);
        dto.setName("Arquivo");
        dto.setSupplierId(1L);
        return dto;
    }

    private SupplierFileViewDTO createViewDTO() {
        return new SupplierFileViewDTO("arquivo.txt", MediaType.TEXT_PLAIN_VALUE, new byte[]{1});
    }
}
