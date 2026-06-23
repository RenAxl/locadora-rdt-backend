package com.locadora_rdt_backend.tests.modules.departments.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locadora_rdt_backend.modules.departments.controller.DepartmentController;
import com.locadora_rdt_backend.modules.departments.dto.DepartmentDTO;
import com.locadora_rdt_backend.modules.departments.dto.DepartmentDetailsDTO;
import com.locadora_rdt_backend.modules.departments.dto.DepartmentInsertDTO;
import com.locadora_rdt_backend.modules.departments.dto.DepartmentUpdateDTO;
import com.locadora_rdt_backend.modules.departments.service.DepartmentService;
import com.locadora_rdt_backend.tests.modules.departments.factory.DepartmentFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DepartmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class DepartmentControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DepartmentService service;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private UserDetailsService userDetailsService;

    private final Long existingId = 1L;
    private final Long nonExistingId = 999L;

    @Test
    void findAllPagedShouldReturnPage() throws Exception {
        DepartmentDTO dto = DepartmentFactory.createDepartmentDTO();
        Page<DepartmentDTO> page = new PageImpl<>(List.of(dto));

        Mockito.when(service.findAllPaged(anyString(), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(existingId));
    }

    @Test
    void findAllPagedShouldReturnEmptyPage() throws Exception {
        Mockito.when(service.findAllPaged(anyString(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void findAllPagedShouldCallService() throws Exception {
        Mockito.when(service.findAllPaged(anyString(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/departments"));

        Mockito.verify(service).findAllPaged(anyString(), any(PageRequest.class));
    }

    @Test
    void findByIdShouldReturnDTOWhenIdExists() throws Exception {
        DepartmentDetailsDTO dto = DepartmentFactory.createDepartmentDetailsDTO();

        Mockito.when(service.findById(existingId)).thenReturn(dto);

        mockMvc.perform(get("/departments/{id}", existingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingId));
    }

    @Test
    void findByIdShouldReturnErrorWhenIdDoesNotExist() throws Exception {
        Mockito.when(service.findById(nonExistingId)).thenThrow(new RuntimeException());

        mockMvc.perform(get("/departments/{id}", nonExistingId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void insertShouldReturnCreated() throws Exception {
        DepartmentInsertDTO insertDTO = DepartmentFactory.createDepartmentInsertDTO();
        DepartmentDTO dto = DepartmentFactory.createDepartmentDTO();

        Mockito.when(service.insert(any())).thenReturn(dto);

        mockMvc.perform(post("/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(insertDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void insertShouldReturnDTO() throws Exception {
        DepartmentInsertDTO insertDTO = DepartmentFactory.createDepartmentInsertDTO();

        Mockito.when(service.insert(any())).thenReturn(DepartmentFactory.createDepartmentDTO());

        mockMvc.perform(post("/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(insertDTO)))
                .andExpect(jsonPath("$.name").value("Tecnologia"));
    }

    @Test
    void updateShouldReturnOK() throws Exception {
        DepartmentUpdateDTO updateDTO = DepartmentFactory.createDepartmentUpdateDTO();

        Mockito.when(service.update(eq(existingId), any())).thenReturn(DepartmentFactory.createDepartmentDTO());

        mockMvc.perform(put("/departments/{id}", existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void updateShouldReturnUpdatedData() throws Exception {
        DepartmentUpdateDTO updateDTO = DepartmentFactory.createDepartmentUpdateDTO();
        DepartmentDTO dto = new DepartmentDTO(existingId, "Tecnologia Atualizada");

        Mockito.when(service.update(eq(existingId), any())).thenReturn(dto);

        mockMvc.perform(put("/departments/{id}", existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(jsonPath("$.name").value("Tecnologia Atualizada"));
    }

    @Test
    void deleteShouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(service).delete(existingId);

        mockMvc.perform(delete("/departments/{id}", existingId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteShouldReturnError() throws Exception {
        Mockito.doThrow(new RuntimeException()).when(service).delete(nonExistingId);

        mockMvc.perform(delete("/departments/{id}", nonExistingId))
                .andExpect(status().isInternalServerError());
    }
}
