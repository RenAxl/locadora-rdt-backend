package com.locadora_rdt_backend.tests.modules.employees.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locadora_rdt_backend.modules.employees.controller.EmployeeController;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeDTO;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeDetailsDTO;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeInsertDTO;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeUpdateDTO;
import com.locadora_rdt_backend.modules.employees.model.Employee;
import com.locadora_rdt_backend.modules.employees.repository.EmployeeRepository;
import com.locadora_rdt_backend.modules.employees.service.EmployeeService;
import com.locadora_rdt_backend.tests.factories.EmployeeFactory;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EmployeeControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService service;

    @MockBean
    private EmployeeRepository repository;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private UserDetailsService userDetailsService;

    private final Long existingId = 1L;
    private final Long nonExistingId = 999L;

    @Test
    void findAllPagedShouldReturnPage() throws Exception {
        EmployeeDTO dto = EmployeeFactory.createEmployeeDTO();
        Page<EmployeeDTO> page = new PageImpl<>(List.of(dto));

        Mockito.when(service.findAllPaged(anyString(), any(PageRequest.class)))
                .thenReturn(page);

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(existingId))
                .andExpect(jsonPath("$.content[0].name").value("João da Silva"));
    }

    @Test
    void findAllPagedShouldReturnEmptyPage() throws Exception {
        Page<EmployeeDTO> page = new PageImpl<>(List.of());

        Mockito.when(service.findAllPaged(anyString(), any(PageRequest.class)))
                .thenReturn(page);

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void findAllPagedShouldCallService() throws Exception {
        Mockito.when(service.findAllPaged(anyString(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/employees"));

        Mockito.verify(service).findAllPaged(anyString(), any(PageRequest.class));
    }

    @Test
    void findByIdShouldReturnDTOWhenIdExists() throws Exception {
        EmployeeDetailsDTO dto = EmployeeFactory.createEmployeeDetailsDTO();

        Mockito.when(service.findById(existingId)).thenReturn(dto);

        mockMvc.perform(get("/employees/{id}", existingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingId))
                .andExpect(jsonPath("$.name").value(dto.getName()));
    }

    @Test
    void findByIdShouldReturnErrorWhenIdDoesNotExist() throws Exception {
        Mockito.when(service.findById(nonExistingId))
                .thenThrow(new RuntimeException());

        mockMvc.perform(get("/employees/{id}", nonExistingId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void findByIdShouldCallService() throws Exception {
        Mockito.when(service.findById(existingId))
                .thenReturn(EmployeeFactory.createEmployeeDetailsDTO());

        mockMvc.perform(get("/employees/{id}", existingId));

        Mockito.verify(service).findById(existingId);
    }

    @Test
    void insertShouldReturnCreated() throws Exception {
        EmployeeInsertDTO insertDTO = EmployeeFactory.createEmployeeInsertDTO();
        EmployeeDTO dto = EmployeeFactory.createEmployeeDTO();

        Mockito.when(service.insert(any())).thenReturn(dto);

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(insertDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void insertShouldReturnDTO() throws Exception {
        EmployeeInsertDTO insertDTO = EmployeeFactory.createEmployeeInsertDTO();
        EmployeeDTO dto = EmployeeFactory.createEmployeeDTO();

        Mockito.when(service.insert(any())).thenReturn(dto);

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(insertDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("João da Silva"));
    }

    @Test
    void insertShouldCallService() throws Exception {
        EmployeeInsertDTO insertDTO = EmployeeFactory.createEmployeeInsertDTO();

        Mockito.when(service.insert(any()))
                .thenReturn(EmployeeFactory.createEmployeeDTO());

        mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(insertDTO)));

        Mockito.verify(service).insert(any());
    }

    @Test
    void updateShouldReturnOK() throws Exception {
        EmployeeUpdateDTO updateDTO = EmployeeFactory.createEmployeeUpdateDTO();
        EmployeeDTO dto = EmployeeFactory.createEmployeeDTO();

        Mockito.when(service.update(eq(existingId), any())).thenReturn(dto);

        mockMvc.perform(put("/employees/{id}", existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void updateShouldReturnUpdatedDTO() throws Exception {
        EmployeeUpdateDTO updateDTO = EmployeeFactory.createEmployeeUpdateDTO();
        EmployeeDTO dto = EmployeeFactory.createEmployeeDTO();

        dto.setName("João Atualizado");

        Mockito.when(service.update(eq(existingId), any())).thenReturn(dto);

        mockMvc.perform(put("/employees/{id}", existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("João Atualizado"));
    }

    @Test
    void updateShouldCallService() throws Exception {
        EmployeeUpdateDTO updateDTO = EmployeeFactory.createEmployeeUpdateDTO();

        Mockito.when(service.update(eq(existingId), any()))
                .thenReturn(EmployeeFactory.createEmployeeDTO());

        mockMvc.perform(put("/employees/{id}", existingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)));

        Mockito.verify(service).update(eq(existingId), any());
    }

    @Test
    void updatePhotoShouldReturnNoContent() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.png",
                "image/png",
                "fake-image".getBytes()
        );

        Mockito.doNothing().when(service).updatePhoto(eq(existingId), any());

        mockMvc.perform(multipart("/employees/{id}/photo", existingId)
                        .file(file)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isNoContent());
    }

    @Test
    void updatePhotoShouldReturnError() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.png",
                "image/png",
                "fake-image".getBytes()
        );

        Mockito.doThrow(new RuntimeException())
                .when(service).updatePhoto(eq(nonExistingId), any());

        mockMvc.perform(multipart("/employees/{id}/photo", nonExistingId)
                        .file(file)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updatePhotoShouldCallService() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.png",
                "image/png",
                "fake-image".getBytes()
        );

        Mockito.doNothing().when(service).updatePhoto(eq(existingId), any());

        mockMvc.perform(multipart("/employees/{id}/photo", existingId)
                .file(file)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }));

        Mockito.verify(service).updatePhoto(eq(existingId), any());
    }

    @Test
    void getPhotoShouldReturnPhoto() throws Exception {
        Employee employee = EmployeeFactory.createEmployee();

        Mockito.when(service.findEntityById(existingId)).thenReturn(employee);

        mockMvc.perform(get("/employees/{id}/photo", existingId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/png"));
    }

    @Test
    void getPhotoShouldReturnNoContentWhenPhotoIsNull() throws Exception {
        Employee employee = EmployeeFactory.createEmployee();
        employee.setPhoto(null);

        Mockito.when(service.findEntityById(existingId)).thenReturn(employee);

        mockMvc.perform(get("/employees/{id}/photo", existingId))
                .andExpect(status().isNoContent());
    }

    @Test
    void getPhotoShouldCallService() throws Exception {
        Employee employee = EmployeeFactory.createEmployee();

        Mockito.when(service.findEntityById(existingId)).thenReturn(employee);

        mockMvc.perform(get("/employees/{id}/photo", existingId));

        Mockito.verify(service).findEntityById(existingId);
    }

    @Test
    void deleteShouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(service).delete(existingId);

        mockMvc.perform(delete("/employees/{id}", existingId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteShouldReturnError() throws Exception {
        Mockito.doThrow(new RuntimeException()).when(service).delete(nonExistingId);

        mockMvc.perform(delete("/employees/{id}", nonExistingId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void deleteShouldCallService() throws Exception {
        Mockito.doNothing().when(service).delete(existingId);

        mockMvc.perform(delete("/employees/{id}", existingId));

        Mockito.verify(service).delete(existingId);
    }

    @Test
    void deleteAllShouldReturnNoContent() throws Exception {
        List<Long> ids = List.of(1L, 2L);

        Mockito.doNothing().when(service).deleteAll(ids);

        mockMvc.perform(delete("/employees/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteAllShouldReturnError() throws Exception {
        List<Long> ids = List.of(1L, 2L);

        Mockito.doThrow(new RuntimeException()).when(service).deleteAll(ids);

        mockMvc.perform(delete("/employees/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void deleteAllShouldCallService() throws Exception {
        List<Long> ids = List.of(1L, 2L);

        Mockito.doNothing().when(service).deleteAll(ids);

        mockMvc.perform(delete("/employees/all")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ids)));

        Mockito.verify(service).deleteAll(ids);
    }

    @Test
    void changeActiveShouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(service).changeActiveStatus(existingId, true);

        mockMvc.perform(patch("/employees/{id}/active", existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("true"))
                .andExpect(status().isNoContent());
    }

    @Test
    void changeActiveShouldReturnError() throws Exception {
        Mockito.doThrow(new RuntimeException())
                .when(service).changeActiveStatus(nonExistingId, true);

        mockMvc.perform(patch("/employees/{id}/active", nonExistingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("true"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void changeActiveShouldCallService() throws Exception {
        Mockito.doNothing().when(service).changeActiveStatus(existingId, true);

        mockMvc.perform(patch("/employees/{id}/active", existingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("true"));

        Mockito.verify(service).changeActiveStatus(existingId, true);
    }
}