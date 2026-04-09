package com.locadora_rdt_backend.tests.modules.employees.positions.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locadora_rdt_backend.modules.employees.positions.controller.PositionController;
import com.locadora_rdt_backend.modules.employees.positions.dto.PositionDTO;
import com.locadora_rdt_backend.modules.employees.positions.service.PositionService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PositionController.class)
public class PositionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PositionService service;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private UserDetailsService userDetailsService;


    @Test
    public void findAllPagedShouldReturnPagedPositions() throws Exception {

        PositionDTO dto = new PositionDTO();
        dto.setId(1L);
        dto.setName("Gerente");

        Page<PositionDTO> page = new PageImpl<>(List.of(dto));

        PageRequest expectedPageRequest =
                PageRequest.of(0, 3, Sort.Direction.ASC, "name");

        Mockito.when(service.findAllPaged(eq("Gerente"), eq(expectedPageRequest)))
                .thenReturn(page);

        mockMvc.perform(get("/positions")
                        .param("name", "Gerente")
                        .param("page", "0")
                        .param("linesPerPage", "3")
                        .param("direction", "ASC")
                        .param("orderBy", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].name", is("Gerente")))
                .andExpect(jsonPath("$.totalElements", is(1)));

        Mockito.verify(service, Mockito.times(1))
                .findAllPaged(eq("Gerente"), eq(expectedPageRequest));
    }

    @Test
    public void findAllPagedShouldUseDefaultParams() throws Exception {

        Page<PositionDTO> page = new PageImpl<>(List.of());

        PageRequest expectedPageRequest =
                PageRequest.of(0, 3, Sort.Direction.ASC, "name");

        Mockito.when(service.findAllPaged(eq(""), eq(expectedPageRequest)))
                .thenReturn(page);

        mockMvc.perform(get("/positions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements", is(0)));

        Mockito.verify(service, Mockito.times(1))
                .findAllPaged(eq(""), eq(expectedPageRequest));
    }

    @Test
    public void findAllPagedShouldTrimName() throws Exception {

        Page<PositionDTO> page = new PageImpl<>(List.of());

        PageRequest expectedPageRequest =
                PageRequest.of(0, 3, Sort.Direction.ASC, "name");

        Mockito.when(service.findAllPaged(eq("Gerente"), eq(expectedPageRequest)))
                .thenReturn(page);

        mockMvc.perform(get("/positions")
                        .param("name", "   Gerente   "))
                .andExpect(status().isOk());

        Mockito.verify(service, Mockito.times(1))
                .findAllPaged(eq("Gerente"), eq(expectedPageRequest));
    }

    @Test
    @WithMockUser
    public void insertShouldReturnCreatedAndPositionDTO() throws Exception {

        PositionDTO requestDTO = new PositionDTO();
        requestDTO.setName("Gerente");

        PositionDTO responseDTO = new PositionDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Gerente");

        Mockito.when(service.insert(ArgumentMatchers.any(PositionDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/positions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Gerente")));

        Mockito.verify(service, Mockito.times(1))
                .insert(ArgumentMatchers.any(PositionDTO.class));
    }

    @Test
    @WithMockUser
    public void insertShouldReturnLocationHeader() throws Exception {

        PositionDTO requestDTO = new PositionDTO();
        requestDTO.setName("Gerente");

        PositionDTO responseDTO = new PositionDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Gerente");

        Mockito.when(service.insert(ArgumentMatchers.any(PositionDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/positions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/positions/1"));
    }

    @Test
    @WithMockUser
    public void insertShouldCallServiceInsert() throws Exception {

        PositionDTO requestDTO = new PositionDTO();
        requestDTO.setName("Atendente");

        PositionDTO responseDTO = new PositionDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Atendente");

        Mockito.when(service.insert(ArgumentMatchers.any(PositionDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/positions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated());

        Mockito.verify(service, Mockito.times(1))
                .insert(ArgumentMatchers.any(PositionDTO.class));
    }
}