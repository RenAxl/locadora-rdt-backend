package com.locadora_rdt_backend.services;

import com.locadora_rdt_backend.dto.UserDTO;
import com.locadora_rdt_backend.dto.UserInsertDTO;
import com.locadora_rdt_backend.entities.User;
import com.locadora_rdt_backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    @Transactional(readOnly = true)
    public Page<UserDTO> findAllPaged(String name, PageRequest pageRequest) {
        Page<User> list = repository.find(name, pageRequest);
        Page<UserDTO> listDto = list.map(user -> new UserDTO(user));

        return listDto;
    }

    @Transactional
    public UserDTO insert(UserInsertDTO dto) {
        User entity = new User();
        copyDtoInsertToEntity(dto, entity);
        entity = repository.save(entity);

        return new UserDTO(entity);
    }

    private void copyDtoInsertToEntity(UserInsertDTO dto, User entity) {
        entity.setName(dto.getName());
        entity.setPassword(dto.getPassword());
        entity.setEmail(dto.getEmail());
        entity.setProfile(dto.getProfile());
        entity.setActive(dto.getActive());
        entity.setTelephone(dto.getTelephone());
        entity.setAddress(dto.getAddress());
        entity.setPhoto(dto.getPhoto());
        entity.setDate(dto.getDate());
    }

}
