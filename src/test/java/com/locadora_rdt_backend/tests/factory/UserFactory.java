package com.locadora_rdt_backend.tests.factory;

import java.time.Instant;

import com.locadora_rdt_backend.dto.UserDTO;

import com.locadora_rdt_backend.dto.UserInsertDTO;
import com.locadora_rdt_backend.entities.User;
import com.locadora_rdt_backend.entities.enums.UserProfile;

public class UserFactory {

    public static User createUser() {
        return new User(
                1L,
                "Renan Duarte",
                "renan@email.com",
                "123456",
                UserProfile.ADMINISTRADOR,
                "true",
                "31999999999",
                "Rua A, 123",
                "sem-foto.jpg",
                Instant.parse("2025-12-22T10:15:30Z")
        );
    }

    public static UserDTO createUserDTO() {
        User user = createUser();
        return new UserDTO(user);
    }

    public static UserInsertDTO createUserInsertDTO() {
        UserInsertDTO dto = new UserInsertDTO();
        dto.setName("Renan Duarte");
        dto.setEmail("renan@email.com");
        dto.setPassword("123456");
        dto.setProfile(UserProfile.ADMINISTRADOR);
        dto.setActive("true");
        dto.setTelephone("31999999999");
        dto.setAddress("Rua A, 123");
        dto.setPhoto("sem-foto.jpg");
        dto.setDate(Instant.parse("2025-12-22T10:15:30Z")); // se existir no DTO
        return dto;
    }
}

