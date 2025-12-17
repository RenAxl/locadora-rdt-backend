package com.locadora_rdt_backend.services;

import com.locadora_rdt_backend.dto.UserDTO;
import com.locadora_rdt_backend.entities.User;
import com.locadora_rdt_backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

}
