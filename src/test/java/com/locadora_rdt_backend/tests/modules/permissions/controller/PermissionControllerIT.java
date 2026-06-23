package com.locadora_rdt_backend.tests.modules.permissions.controller;

import com.locadora_rdt_backend.modules.permissions.controller.PermissionController;
import com.locadora_rdt_backend.modules.permissions.dto.PermissionDTO;
import com.locadora_rdt_backend.modules.permissions.service.PermissionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PermissionController.class)
@AutoConfigureMockMvc(addFilters = false)
class PermissionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PermissionService service;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void findAllShouldReturnList() throws Exception {
        Mockito.when(service.findAll(anyString())).thenReturn(List.of(new PermissionDTO(1L, "READ", "Users")));

        mockMvc.perform(get("/permissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("READ"));
    }

    @Test
    void findAllGroupsShouldReturnList() throws Exception {
        Mockito.when(service.findAllGroupNames()).thenReturn(List.of("Users"));

        mockMvc.perform(get("/permissions/groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Users"));
    }
}
