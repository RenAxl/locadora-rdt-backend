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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
class EmployeeControllerIT {

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

    @Test
    void findAllPagedShouldReturnPage() throws Exception {
        Mockito.when(service.findAllPaged(anyString(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(createDTO())));

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    void findByIdShouldReturnDTO() throws Exception {
        EmployeeDetailsDTO dto = new EmployeeDetailsDTO();
        dto.setId(1L);
        dto.setName("Funcionario");
        Mockito.when(service.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/employees/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Funcionario"));
    }

    @Test
    void insertShouldReturnCreated() throws Exception {
        Mockito.when(service.insert(any())).thenReturn(createDTO());

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createInsertDTO())))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void updateShouldReturnOK() throws Exception {
        Mockito.when(service.update(eq(1L), any())).thenReturn(createDTO());

        mockMvc.perform(put("/employees/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUpdateDTO())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void updatePhotoShouldReturnNoContent() throws Exception {
        mockMvc.perform(multipart("/employees/{id}/photo", 1L)
                        .file(new MockMultipartFile("file", "photo.png", "image/png", new byte[]{1}))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isNoContent());

        Mockito.verify(service).updatePhoto(eq(1L), any());
    }

    @Test
    void getPhotoShouldReturnBytesWhenPhotoExists() throws Exception {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setPhoto(new byte[]{1});
        employee.setPhotoContentType("image/png");
        Mockito.when(service.findEntityById(1L)).thenReturn(employee);

        mockMvc.perform(get("/employees/{id}/photo", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG));
    }

    @Test
    void getPhotoShouldReturnNoContentWhenPhotoDoesNotExist() throws Exception {
        Mockito.when(service.findEntityById(1L)).thenReturn(new Employee());

        mockMvc.perform(get("/employees/{id}/photo", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/employees/{id}", 1L))
                .andExpect(status().isNoContent());

        Mockito.verify(service).delete(1L);
    }

    @Test
    void deleteAllShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/employees/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(1L))))
                .andExpect(status().isNoContent());
    }

    @Test
    void changeActiveShouldReturnNoContent() throws Exception {
        mockMvc.perform(patch("/employees/{id}/active", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("true"))
                .andExpect(status().isNoContent());

        Mockito.verify(service).changeActiveStatus(1L, true);
    }

    private EmployeeDTO createDTO() {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(1L);
        dto.setName("Funcionario");
        return dto;
    }

    private EmployeeInsertDTO createInsertDTO() {
        EmployeeInsertDTO dto = new EmployeeInsertDTO();
        fill(dto);
        return dto;
    }

    private EmployeeUpdateDTO createUpdateDTO() {
        EmployeeUpdateDTO dto = new EmployeeUpdateDTO();
        fill(dto);
        return dto;
    }

    private void fill(EmployeeInsertDTO dto) {
        dto.setName("Funcionario");
        dto.setEmployeeCode("EMP001");
        dto.setEmail("funcionario@email.com");
        dto.setPhone("11999999999");
        dto.setAddress("Rua A");
        dto.setSalary(BigDecimal.TEN);
        dto.setHireDate(LocalDate.of(2026, Month.JANUARY, 1));
        dto.setEmploymentType("CLT");
        dto.setPositionId(1L);
        dto.setDepartmentId(1L);
    }

    private void fill(EmployeeUpdateDTO dto) {
        dto.setName("Funcionario");
        dto.setEmployeeCode("EMP001");
        dto.setEmail("funcionario@email.com");
        dto.setPhone("11999999999");
        dto.setAddress("Rua A");
        dto.setSalary(BigDecimal.TEN);
        dto.setHireDate(LocalDate.of(2026, Month.JANUARY, 1));
        dto.setEmploymentType("CLT");
        dto.setActive(true);
        dto.setPositionId(1L);
        dto.setDepartmentId(1L);
    }
}
