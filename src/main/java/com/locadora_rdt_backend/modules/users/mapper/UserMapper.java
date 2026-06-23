package com.locadora_rdt_backend.modules.users.mapper;

import com.locadora_rdt_backend.modules.roles.mapper.RoleMapper;
import com.locadora_rdt_backend.modules.roles.model.Role;
import com.locadora_rdt_backend.modules.users.dto.UserDTO;
import com.locadora_rdt_backend.modules.users.dto.UserDetailsDTO;
import com.locadora_rdt_backend.modules.users.dto.UserInsertDTO;
import com.locadora_rdt_backend.modules.users.dto.UserUpdateDTO;
import com.locadora_rdt_backend.modules.users.model.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    private final RoleMapper roleMapper;

    public UserMapper(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    public UserDTO toDTO(User entity) {

        UserDTO dto = new UserDTO();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());

        dto.setActive(entity.getActive());

        dto.setTelephone(entity.getTelephone());
        dto.setAddress(entity.getAddress());

        dto.setPhotoContentType(entity.getPhotoContentType());

        dto.setRoles(
                entity.getRoles()
                        .stream()
                        .map(roleMapper::toDTO)
                        .collect(Collectors.toList())
        );

        return dto;
    }

    public UserDetailsDTO toDetailsDTO(User entity) {

        UserDetailsDTO dto = new UserDetailsDTO();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());

        dto.setActive(entity.getActive());

        dto.setTelephone(entity.getTelephone());
        dto.setAddress(entity.getAddress());

        dto.setPhotoContentType(entity.getPhotoContentType());

        dto.setRoles(
                entity.getRoles()
                        .stream()
                        .map(Role::getAuthority)
                        .collect(Collectors.toList())
        );

        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUpdatedBy(entity.getUpdatedBy());

        return dto;
    }

    public User toEntity(UserInsertDTO dto) {

        User entity = new User();

        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setTelephone(dto.getTelephone());
        entity.setAddress(dto.getAddress());

        entity.setActive(true);

        return entity;
    }

    public void updateEntity(User entity, UserUpdateDTO dto) {

        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());

        entity.setTelephone(dto.getTelephone());
        entity.setAddress(dto.getAddress());

        if (dto.getActive() != null) {
            entity.setActive(dto.getActive());
        }
    }
}
