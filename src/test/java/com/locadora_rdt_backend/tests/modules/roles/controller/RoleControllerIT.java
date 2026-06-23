package com.locadora_rdt_backend.tests.modules.roles.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locadora_rdt_backend.modules.roles.controller.RoleController;
import com.locadora_rdt_backend.modules.roles.dto.RoleDTO;
import com.locadora_rdt_backend.modules.roles.dto.RoleDetailsDTO;
import com.locadora_rdt_backend.modules.roles.dto.RoleInsertDTO;
import com.locadora_rdt_backend.modules.roles.dto.RolePermissionsUpdateDTO;
import com.locadora_rdt_backend.modules.roles.service.RoleService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoleController.class)
@AutoConfigureMockMvc(addFilters = false)
class RoleControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RoleService service;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void findAllPagedShouldReturnPage() throws Exception {
        Mockito.when(service.findAllPaged(anyString(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(createRoleDTO())));

        mockMvc.perform(get("/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].authority").value("ROLE_ADMIN"));
    }

    @Test
    void findByIdShouldReturnDTO() throws Exception {
        RoleDetailsDTO dto = new RoleDetailsDTO();
        dto.setId(1L);
        dto.setAuthority("ROLE_ADMIN");
        Mockito.when(service.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/roles/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void updatePermissionsShouldReturnOK() throws Exception {
        RolePermissionsUpdateDTO dto = new RolePermissionsUpdateDTO();
        dto.setPermissionIds(List.of(1L));
        Mockito.when(service.updateRolePermissions(eq(1L), any())).thenReturn(createRoleDTO());

        mockMvc.perform(put("/roles/{id}/permissions", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authority").value("ROLE_ADMIN"));
    }

    @Test
    void insertShouldReturnCreated() throws Exception {
        RoleInsertDTO dto = new RoleInsertDTO();
        dto.setAuthority("ROLE_ADMIN");
        Mockito.when(service.insert(any())).thenReturn(createRoleDTO());

        mockMvc.perform(post("/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    private RoleDTO createRoleDTO() {
        RoleDTO dto = new RoleDTO();
        dto.setId(1L);
        dto.setAuthority("ROLE_ADMIN");
        return dto;
    }
}
