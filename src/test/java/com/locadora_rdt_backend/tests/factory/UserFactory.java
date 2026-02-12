package com.locadora_rdt_backend.tests.factory;

import java.time.Instant;
import java.util.List;

import com.locadora_rdt_backend.dto.RoleDTO;
import com.locadora_rdt_backend.dto.UserDTO;
import com.locadora_rdt_backend.dto.UserInsertDTO;
import com.locadora_rdt_backend.dto.UserUpdateDTO;
import com.locadora_rdt_backend.entities.Role;
import com.locadora_rdt_backend.entities.User;

public class UserFactory {

    public static User createUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Renan Duarte");
        user.setEmail("renan@email.com");
        user.setPassword("123456"); // só pra testes
        user.setActive(true);
        user.setTelephone("31999999999");
        user.setAddress("Rua A, 123");

        // ✅ agora é String (ex: URL, nome do arquivo, base64, etc.)
        user.setPhoto("fake-photo");
        user.setDate(Instant.now());

        Role role = createRole();
        user.getRoles().add(role);

        return user;
    }

    public static UserDTO createUserDTO() {
        return new UserDTO(createUser());
    }

    public static Role createRole() {
        Role role = new Role();
        role.setId(1L);
        role.setAuthority("ROLE_ADMIN");
        return role;
    }

    public static RoleDTO createRoleDTO() {
        RoleDTO dto = new RoleDTO();
        dto.setId(1L);
        dto.setAuthority("ROLE_ADMIN");
        return dto;
    }

    public static UserInsertDTO createUserInsertDTO() {
        UserInsertDTO dto = new UserInsertDTO();
        dto.setName("Renan Duarte");
        dto.setEmail("renan@email.com");
        dto.setTelephone("31999999999");
        dto.setAddress("Rua A, 123");
        dto.setPhoto("fake-photo");
        dto.setRoles(List.of(createRoleDTO()));
        return dto;
    }

    public static UserUpdateDTO createUserUpdateDTO() {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setName("Novo Nome");
        dto.setEmail("novo@email.com");
        dto.setActive(false);
        dto.setTelephone("31988887777");
        dto.setAddress("Rua B, 456");
        dto.setPhoto("updated-photo");
        dto.setRoles(List.of(createRoleDTO()));
        return dto;
    }
}
