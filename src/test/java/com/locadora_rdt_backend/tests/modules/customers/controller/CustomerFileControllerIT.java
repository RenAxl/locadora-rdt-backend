package com.locadora_rdt_backend.tests.modules.customers.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.customers.controller.CustomerFileController;
import com.locadora_rdt_backend.modules.customers.dto.file.CustomerFileDTO;
import com.locadora_rdt_backend.modules.customers.dto.file.CustomerFileResponseDTO;
import com.locadora_rdt_backend.modules.customers.service.CustomerFileService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerFileController.class)
public class CustomerFileControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerFileService service;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private UserDetailsService userDetailsService;

    private Long customerId = 1L;
    private Long fileId = 10L;


    @Test
    @WithMockUser
    public void uploadShouldReturnCreated() throws Exception {

        CustomerFileDTO dto = new CustomerFileDTO();
        dto.setId(fileId);
        dto.setName("Contrato");

        MockMultipartFile file = new MockMultipartFile(
                "file", "file.pdf", "application/pdf", "content".getBytes()
        );

        Mockito.when(service.upload(eq(customerId), eq("Contrato"), ArgumentMatchers.any()))
                .thenReturn(dto);

        mockMvc.perform(multipart("/customers/{customerId}/files", customerId)
                        .file(file)
                        .param("name", "Contrato")
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(fileId.intValue())))
                .andExpect(header().exists("Location"));

        Mockito.verify(service).upload(eq(customerId), eq("Contrato"), ArgumentMatchers.any());
    }

    @Test
    @WithMockUser
    public void uploadShouldReturnBadRequestWhenFileMissing() throws Exception {

        mockMvc.perform(multipart("/customers/{customerId}/files", customerId)
                        .param("name", "Contrato")
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void uploadShouldReturnNotFoundWhenCustomerNotExists() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file", "file.pdf", "application/pdf", "content".getBytes()
        );

        Mockito.when(service.upload(eq(customerId), eq("Contrato"), ArgumentMatchers.any()))
                .thenThrow(new ResourceNotFoundException("Cliente não encontrado"));

        mockMvc.perform(multipart("/customers/{customerId}/files", customerId)
                        .file(file)
                        .param("name", "Contrato")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }


    @Test
    public void findAllShouldReturnList() throws Exception {

        CustomerFileDTO dto = new CustomerFileDTO();
        dto.setId(fileId);
        dto.setName("Contrato");

        Mockito.when(service.findAllByCustomer(customerId))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/customers/{customerId}/files", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(fileId.intValue())))
                .andExpect(jsonPath("$[0].name", is("Contrato")));
    }

    @Test
    public void findAllShouldReturnEmptyList() throws Exception {

        Mockito.when(service.findAllByCustomer(customerId))
                .thenReturn(List.of());

        mockMvc.perform(get("/customers/{customerId}/files", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void findAllShouldReturnNotFound() throws Exception {

        Mockito.when(service.findAllByCustomer(customerId))
                .thenThrow(new ResourceNotFoundException("Cliente não encontrado"));

        mockMvc.perform(get("/customers/{customerId}/files", customerId))
                .andExpect(status().isNotFound());
    }


    @Test
    public void viewShouldReturnInlineFile() throws Exception {

        byte[] data = "content".getBytes();

        CustomerFileResponseDTO dto =
                new CustomerFileResponseDTO("file.pdf", "application/pdf", data);

        Mockito.when(service.download(customerId, fileId))
                .thenReturn(dto);

        mockMvc.perform(get("/customers/{customerId}/files/{fileId}/view", customerId, fileId))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("inline")))
                .andExpect(content().bytes(data));
    }

    @Test
    public void downloadShouldReturnAttachment() throws Exception {

        byte[] data = "content".getBytes();

        CustomerFileResponseDTO dto =
                new CustomerFileResponseDTO("file.pdf", "application/pdf", data);

        Mockito.when(service.download(customerId, fileId))
                .thenReturn(dto);

        mockMvc.perform(get("/customers/{customerId}/files/{fileId}/download", customerId, fileId))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("attachment")));
    }

    @Test
    public void viewShouldReturnNotFound() throws Exception {

        Mockito.when(service.download(customerId, fileId))
                .thenThrow(new ResourceNotFoundException("Arquivo não encontrado"));

        mockMvc.perform(get("/customers/{customerId}/files/{fileId}/view", customerId, fileId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void deleteShouldReturnNoContent() throws Exception {

        Mockito.doNothing().when(service).delete(customerId, fileId);

        mockMvc.perform(delete("/customers/{customerId}/files/{fileId}", customerId, fileId)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void deleteShouldReturnNotFound() throws Exception {

        Mockito.doThrow(new ResourceNotFoundException("Arquivo não encontrado"))
                .when(service).delete(customerId, fileId);

        mockMvc.perform(delete("/customers/{customerId}/files/{fileId}", customerId, fileId)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void deleteShouldCallService() throws Exception {

        Mockito.doNothing().when(service).delete(customerId, fileId);

        mockMvc.perform(delete("/customers/{customerId}/files/{fileId}", customerId, fileId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        Mockito.verify(service).delete(customerId, fileId);
    }
}