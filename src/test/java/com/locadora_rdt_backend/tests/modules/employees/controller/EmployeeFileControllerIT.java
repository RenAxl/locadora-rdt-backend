package com.locadora_rdt_backend.tests.modules.employees.controller;

import com.locadora_rdt_backend.modules.employees.controller.EmployeeFileController;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeFileDTO;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeFileViewDTO;
import com.locadora_rdt_backend.modules.employees.service.EmployeeFileService;
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

@WebMvcTest(EmployeeFileController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EmployeeFileControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeFileService service;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void uploadShouldReturnCreated() throws Exception {
        Mockito.when(service.upload(eq(1L), eq("Arquivo"), any())).thenReturn(createDTO());

        mockMvc.perform(multipart("/employees/{employeeId}/files", 1L)
                        .file(new MockMultipartFile("file", "arquivo.txt", "text/plain", new byte[]{1}))
                        .param("name", "Arquivo"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L));
    }

    @Test
    void findAllByEmployeeShouldReturnList() throws Exception {
        Mockito.when(service.findAllByEmployee(1L)).thenReturn(List.of(createDTO()));

        mockMvc.perform(get("/employees/{employeeId}/files", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L));
    }

    @Test
    void viewShouldReturnInlineFile() throws Exception {
        Mockito.when(service.download(1L, 2L)).thenReturn(createViewDTO());

        mockMvc.perform(get("/employees/{employeeId}/files/{fileId}/view", 1L, 2L))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "inline; filename=\"arquivo.txt\""));
    }

    @Test
    void downloadShouldReturnAttachmentFile() throws Exception {
        Mockito.when(service.download(1L, 2L)).thenReturn(createViewDTO());

        mockMvc.perform(get("/employees/{employeeId}/files/{fileId}/download", 1L, 2L))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"arquivo.txt\""));
    }

    @Test
    void deleteShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/employees/{employeeId}/files/{fileId}", 1L, 2L))
                .andExpect(status().isNoContent());
    }

    private EmployeeFileDTO createDTO() {
        EmployeeFileDTO dto = new EmployeeFileDTO();
        dto.setId(2L);
        dto.setName("Arquivo");
        dto.setEmployeeId(1L);
        return dto;
    }

    private EmployeeFileViewDTO createViewDTO() {
        return new EmployeeFileViewDTO("arquivo.txt", MediaType.TEXT_PLAIN_VALUE, new byte[]{1});
    }
}
