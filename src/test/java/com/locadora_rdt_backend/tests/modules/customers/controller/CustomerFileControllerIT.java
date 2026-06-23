package com.locadora_rdt_backend.tests.modules.customers.controller;

import com.locadora_rdt_backend.modules.customers.controller.CustomerFileController;
import com.locadora_rdt_backend.modules.customers.dto.CustomerFileDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerFileViewDTO;
import com.locadora_rdt_backend.modules.customers.service.CustomerFileService;
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

@WebMvcTest(CustomerFileController.class)
@AutoConfigureMockMvc(addFilters = false)
class CustomerFileControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerFileService service;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void uploadShouldReturnCreated() throws Exception {
        CustomerFileDTO dto = createFileDTO();
        Mockito.when(service.upload(eq(1L), eq("Contrato"), any())).thenReturn(dto);

        mockMvc.perform(multipart("/customers/{customerId}/files", 1L)
                        .file(new MockMultipartFile("file", "contrato.pdf", "application/pdf", new byte[]{1}))
                        .param("name", "Contrato"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(2L));
    }

    @Test
    void findAllByCustomerShouldReturnList() throws Exception {
        Mockito.when(service.findAllByCustomer(1L)).thenReturn(List.of(createFileDTO()));

        mockMvc.perform(get("/customers/{customerId}/files", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L));
    }

    @Test
    void viewShouldReturnInlineFile() throws Exception {
        Mockito.when(service.download(1L, 2L)).thenReturn(createViewDTO());

        mockMvc.perform(get("/customers/{customerId}/files/{fileId}/view", 1L, 2L))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "inline; filename=\"arquivo.txt\""))
                .andExpect(content().bytes(new byte[]{1}));
    }

    @Test
    void downloadShouldReturnAttachmentFile() throws Exception {
        Mockito.when(service.download(1L, 2L)).thenReturn(createViewDTO());

        mockMvc.perform(get("/customers/{customerId}/files/{fileId}/download", 1L, 2L))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"arquivo.txt\""));
    }

    @Test
    void deleteShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/customers/{customerId}/files/{fileId}", 1L, 2L))
                .andExpect(status().isNoContent());

        Mockito.verify(service).delete(1L, 2L);
    }

    private CustomerFileDTO createFileDTO() {
        CustomerFileDTO dto = new CustomerFileDTO();
        dto.setId(2L);
        dto.setName("Contrato");
        dto.setCustomerId(1L);
        return dto;
    }

    private CustomerFileViewDTO createViewDTO() {
        return new CustomerFileViewDTO("arquivo.txt", MediaType.TEXT_PLAIN_VALUE, new byte[]{1});
    }
}
