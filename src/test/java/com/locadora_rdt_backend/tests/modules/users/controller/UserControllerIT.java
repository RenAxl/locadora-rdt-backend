package com.locadora_rdt_backend.tests.modules.users.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locadora_rdt_backend.modules.users.controller.UserController;
import com.locadora_rdt_backend.modules.users.dto.ChangePasswordDTO;
import com.locadora_rdt_backend.modules.users.dto.UserDTO;
import com.locadora_rdt_backend.modules.users.dto.UserDetailsDTO;
import com.locadora_rdt_backend.modules.users.dto.UserInsertDTO;
import com.locadora_rdt_backend.modules.users.dto.UserMeUpdateDTO;
import com.locadora_rdt_backend.modules.users.dto.UserPhotoDTO;
import com.locadora_rdt_backend.modules.users.dto.UserUpdateDTO;
import com.locadora_rdt_backend.modules.users.repository.UserRepository;
import com.locadora_rdt_backend.modules.users.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService service;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void findAllPagedShouldReturnPage() throws Exception {
        Mockito.when(service.findAllPaged(anyString(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(createDTO())));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    void findByIdShouldReturnDTO() throws Exception {
        UserDetailsDTO dto = new UserDetailsDTO();
        dto.setId(1L);
        dto.setName("Usuario Teste");
        Mockito.when(service.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Usuario Teste"));
    }

    @Test
    void insertShouldReturnCreated() throws Exception {
        Mockito.when(service.insert(any())).thenReturn(createDTO());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createInsertDTO())))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void updateShouldReturnOK() throws Exception {
        Mockito.when(service.update(eq(1L), any())).thenReturn(createDTO());

        mockMvc.perform(put("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUpdateDTO())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void deleteShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteAllShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/users/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(1L))))
                .andExpect(status().isNoContent());
    }

    @Test
    void changeActiveShouldReturnNoContent() throws Exception {
        mockMvc.perform(patch("/users/{id}/active", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("false"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getMeShouldReturnDTO() throws Exception {
        Mockito.when(service.getMe(nullable(Authentication.class))).thenReturn(createDTO());

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void changePasswordShouldReturnNoContent() throws Exception {
        ChangePasswordDTO dto = new ChangePasswordDTO("senhaAtual", "novaSenha");

        mockMvc.perform(put("/users/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateMeShouldReturnDTO() throws Exception {
        Mockito.when(service.updateMe(nullable(Authentication.class), any())).thenReturn(createDTO());

        mockMvc.perform(put("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserMeUpdateDTO(
                                "Usuario Teste",
                                "usuario@email.com",
                                "11999999999",
                                "Rua A"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void updateMyPhotoShouldReturnNoContent() throws Exception {
        mockMvc.perform(multipart("/users/me/photo")
                        .file(new MockMultipartFile("file", "photo.png", "image/png", new byte[]{1}))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isNoContent());
    }

    @Test
    void getMyPhotoShouldReturnNoContentWhenPhotoDoesNotExist() throws Exception {
        Mockito.when(service.getMyPhoto(nullable(Authentication.class))).thenReturn(null);

        mockMvc.perform(get("/users/me/photo"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getMyPhotoShouldReturnBytesWhenPhotoExists() throws Exception {
        Mockito.when(service.getMyPhoto(nullable(Authentication.class)))
                .thenReturn(new UserPhotoDTO(new byte[]{1}, "image/png"));

        mockMvc.perform(get("/users/me/photo"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG));
    }

    @Test
    void getUserPhotoByIdShouldReturnNoContentWhenPhotoDoesNotExist() throws Exception {
        Mockito.when(service.getUserPhotoById(1L)).thenReturn(null);

        mockMvc.perform(get("/users/{id}/photo", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void getUserPhotoByIdShouldReturnBytesWhenPhotoExists() throws Exception {
        Mockito.when(service.getUserPhotoById(1L)).thenReturn(new UserPhotoDTO(new byte[]{1}, "image/png"));

        mockMvc.perform(get("/users/{id}/photo", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG));
    }

    private UserDTO createDTO() {
        UserDTO dto = new UserDTO();
        dto.setId(1L);
        dto.setName("Usuario Teste");
        dto.setEmail("usuario@email.com");
        return dto;
    }

    private UserInsertDTO createInsertDTO() {
        UserInsertDTO dto = new UserInsertDTO();
        dto.setName("Usuario Teste");
        dto.setEmail("usuario@email.com");
        dto.setTelephone("11999999999");
        dto.setAddress("Rua A");
        dto.setRoleIds(List.of(1L));
        return dto;
    }

    private UserUpdateDTO createUpdateDTO() {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setName("Usuario Teste");
        dto.setEmail("usuario@email.com");
        dto.setActive(true);
        dto.setTelephone("11999999999");
        dto.setAddress("Rua A");
        dto.setRoleIds(List.of(1L));
        return dto;
    }
}
