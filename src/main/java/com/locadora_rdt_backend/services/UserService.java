package com.locadora_rdt_backend.services;

import com.locadora_rdt_backend.dto.UserDTO;
import com.locadora_rdt_backend.dto.UserInsertDTO;
import com.locadora_rdt_backend.dto.UserUpdateDTO;
import com.locadora_rdt_backend.entities.User;
import com.locadora_rdt_backend.repositories.UserRepository;
import com.locadora_rdt_backend.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

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

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        Optional<User> obj = repository.findById(id);
        User entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));

        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO insert(UserInsertDTO dto) {
        User entity = new User();
        copyDtoInsertToEntity(dto, entity);
        entity = repository.save(entity);

        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO update(Long id, UserUpdateDTO dto) {
        try {
            User entity = repository.getOne(id);
            copyDtoUpdateToEntity(dto, entity);
            entity = repository.save(entity);

            return new UserDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found " + id);
        }
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

    private void copyDtoUpdateToEntity(UserUpdateDTO dto, User entity) {
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setProfile(dto.getProfile());
        entity.setActive(dto.getActive());
        entity.setTelephone(dto.getTelephone());
        entity.setAddress(dto.getAddress());
        entity.setPhoto(dto.getPhoto());
        entity.setDate(dto.getDate());
    }

}
