package com.locadora_rdt_backend.tests.modules.positions.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locadora_rdt_backend.modules.positions.controller.PositionController;
import com.locadora_rdt_backend.modules.positions.dto.PositionDTO;
import com.locadora_rdt_backend.modules.positions.dto.PositionDetailsDTO;
import com.locadora_rdt_backend.modules.positions.dto.PositionInsertDTO;
import com.locadora_rdt_backend.modules.positions.dto.PositionUpdateDTO;
import com.locadora_rdt_backend.modules.positions.service.PositionServiceImpl;
import com.locadora_rdt_backend.tests.modules.positions.factory.PositionFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PositionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PositionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PositionServiceImpl service;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private UserDetailsService userDetailsService;

    private final Long existingId = 1L;
    private final Long nonExistingId = 999L;

    @Test
    void findAllPagedShouldReturnPage() throws Exception {
        PositionDTO dto = PositionFactory.createPositionDTO();
        Page<PositionDTO> page = new PageImpl<>(List.of(dto));

        Mockito.when(service.findAllPaged(anyString(), any(PageRequest.class)))
                .thenReturn(page);

        mockMvc.perform(get("/positions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(existingId));
    }

    @Test
    void findAllPagedShouldReturnEmptyPage() throws Exception {
        Page<PositionDTO> page = new PageImpl<>(List.of());

        Mockito.when(service.findAllPaged(anyString(), any(PageRequest.class)))
                .thenReturn(page);

        mockMvc.perform(get("/positions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void findAllPagedShouldCallService() throws Exception {
        Mockito.when(service.findAllPaged(anyString(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/positions"));

        Mockito.verify(service).findAllPaged(anyString(), any(PageRequest.class));
    }

    @Test
    void findByIdShouldReturnDTOWhenIdExists() throws Exception {
        PositionDetailsDTO dto = PositionFactory.createPositionDetailsDTO();

        Mockito.when(service.findById(existingId)).thenReturn(dto);

        mockMvc.perform(get("/positions/{id}", existingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingId));
    }

    @Test
    void findByIdShouldReturnErrorWhenIdDoesNotExist() throws Exception {
        Mockito.when(service.findById(nonExistingId))
                .thenThrow(new RuntimeException());

        mockMvc.perform(get("/positions/{id}", nonExistingId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void findByIdShouldCallService() throws Exception {
        Mockito.when(service.findById(existingId))
                .thenReturn(PositionFactory.createPositionDetailsDTO());

        mockMvc.perform(get("/positions/{id}", existingId));

        Mockito.verify(service).findById(existingId);
    }

    @Test
    void insertShouldReturnCreated() throws Exception {
        PositionInsertDTO insertDTO = PositionFactory.createPositionInsertDTO();
        PositionDTO dto = PositionFactory.createPositionDTO();

        Mockito.when(service.insert(any())).thenReturn(dto);

        mockMvc.perform(post("/positions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(insertDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void insertShouldReturnDTO() throws Exception {
        PositionInsertDTO insertDTO = PositionFactory.createPositionInsertDTO();
        PositionDTO dto = PositionFactory.createPositionDTO();

        Mockito.when(service.insert(any())).thenReturn(dto);

        mockMvc.perform(post("/positions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(insertDTO)))
                .andExpect(jsonPath("$.name").value("Desenvolvedor Java"));
    }

    @Test
    void insertShouldCallService() throws Exception {
        PositionInsertDTO insertDTO = PositionFactory.createPositionInsertDTO();

        Mockito.when(service.insert(any()))
                .thenReturn(PositionFactory.createPositionDTO());

        mockMvc.perform(post("/positions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(insertDTO)));

        Mockito.verify(service).insert(any());
    }


    @Test
    void updateShouldReturnOK() throws Exception {
        PositionUpdateDTO updateDTO = PositionFactory.createPositionUpdateDTO();
        PositionDTO dto = PositionFactory.createPositionDTO();

        Mockito.when(service.update(eq(existingId), any()))
                .thenReturn(dto);

        mockMvc.perform(put("/positions/{id}", existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void updateShouldReturnUpdatedData() throws Exception {
        PositionUpdateDTO updateDTO = PositionFactory.createPositionUpdateDTO();
        PositionDTO dto = new PositionDTO(existingId, "Desenvolvedor Senior");

        Mockito.when(service.update(eq(existingId), any()))
                .thenReturn(dto);

        mockMvc.perform(put("/positions/{id}", existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(jsonPath("$.name").value("Desenvolvedor Senior"));
    }

    @Test
    void updateShouldCallService() throws Exception {
        PositionUpdateDTO updateDTO = PositionFactory.createPositionUpdateDTO();

        Mockito.when(service.update(eq(existingId), any()))
                .thenReturn(PositionFactory.createPositionDTO());

        mockMvc.perform(put("/positions/{id}", existingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)));

        Mockito.verify(service).update(eq(existingId), any());
    }

    @Test
    void deleteShouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(service).delete(existingId);

        mockMvc.perform(delete("/positions/{id}", existingId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteShouldReturnError() throws Exception {
        Mockito.doThrow(new RuntimeException())
                .when(service).delete(nonExistingId);

        mockMvc.perform(delete("/positions/{id}", nonExistingId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void deleteShouldCallService() throws Exception {
        Mockito.doNothing().when(service).delete(existingId);

        mockMvc.perform(delete("/positions/{id}", existingId));

        Mockito.verify(service).delete(existingId);
    }
}