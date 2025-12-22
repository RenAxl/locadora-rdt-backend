package com.locadora_rdt_backend.tests.services;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.locadora_rdt_backend.dto.UserDTO;
import com.locadora_rdt_backend.dto.UserInsertDTO;
import com.locadora_rdt_backend.entities.User;
import com.locadora_rdt_backend.repositories.UserRepository;
import com.locadora_rdt_backend.services.UserService;
import com.locadora_rdt_backend.tests.factory.UserFactory;

@ExtendWith(SpringExtension.class)
public class UserServiceTests {

    @InjectMocks
    private UserService service;

    @Mock
    private UserRepository repository;

    private User user;
    private PageImpl<User> page;

    @BeforeEach
    void setUp() throws Exception {

        user = UserFactory.createUser();
        page = new PageImpl<>(List.of(user));

        Mockito.when(repository.find(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
                .thenReturn(page);

        Mockito.when(repository.save(ArgumentMatchers.any(User.class)))
                .thenReturn(user);
    }

    @Test
    public void findAllPagedShouldReturnPage() {

        String name = "Renan";
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<UserDTO> result = service.findAllPaged(name, pageRequest);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals(user.getId(), result.getContent().get(0).getId());

        Mockito.verify(repository, Mockito.times(1)).find(name, pageRequest);
    }

    @Test
    public void insertShouldReturnUserDTO() {

        UserInsertDTO dto = UserFactory.createUserInsertDTO();

        UserDTO result = service.insert(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(user.getId(), result.getId());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(repository, Mockito.times(1)).save(captor.capture());

        User savedEntity = captor.getValue();
        Assertions.assertEquals(dto.getName(), savedEntity.getName());
        Assertions.assertEquals(dto.getEmail(), savedEntity.getEmail());
        Assertions.assertEquals(dto.getPassword(), savedEntity.getPassword());
        Assertions.assertEquals(dto.getProfile(), savedEntity.getProfile());
        Assertions.assertEquals(dto.getActive(), savedEntity.getActive());
        Assertions.assertEquals(dto.getTelephone(), savedEntity.getTelephone());
        Assertions.assertEquals(dto.getAddress(), savedEntity.getAddress());
        Assertions.assertEquals(dto.getPhoto(), savedEntity.getPhoto());

        if (dto.getDate() != null) {
            Assertions.assertEquals(dto.getDate(), savedEntity.getDate());
        }
    }

    @Test
    public void findByIdShouldReturnUserDTOWhenIdExists() {

        Long existingId = 1L;

        Mockito.when(repository.findById(existingId)).thenReturn(java.util.Optional.of(user));

        UserDTO result = service.findById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(user.getId(), result.getId());

        Mockito.verify(repository, Mockito.times(1)).findById(existingId);
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Long nonExistingId = 1000L;

        Mockito.when(repository.findById(nonExistingId)).thenReturn(java.util.Optional.empty());

        Assertions.assertThrows(com.locadora_rdt_backend.services.exceptions.ResourceNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });

        Mockito.verify(repository, Mockito.times(1)).findById(nonExistingId);
    }

    @Test
    public void updateShouldReturnUserDTOWhenIdExists() {

        Long existingId = 1L;

        com.locadora_rdt_backend.dto.UserUpdateDTO dto = new com.locadora_rdt_backend.dto.UserUpdateDTO();

        Mockito.when(repository.getOne(existingId)).thenReturn(user);
        Mockito.when(repository.save(ArgumentMatchers.any(User.class))).thenReturn(user);

        UserDTO result = service.update(existingId, dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(user.getId(), result.getId());

        Mockito.verify(repository, Mockito.times(1)).getOne(existingId);
        Mockito.verify(repository, Mockito.times(1)).save(ArgumentMatchers.any(User.class));
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Long nonExistingId = 1000L;

        com.locadora_rdt_backend.dto.UserUpdateDTO dto = new com.locadora_rdt_backend.dto.UserUpdateDTO();

        Mockito.when(repository.getOne(nonExistingId))
                .thenThrow(new javax.persistence.EntityNotFoundException());

        Assertions.assertThrows(com.locadora_rdt_backend.services.exceptions.ResourceNotFoundException.class, () -> {
            service.update(nonExistingId, dto);
        });

        Mockito.verify(repository, Mockito.times(1)).getOne(nonExistingId);
        Mockito.verify(repository, Mockito.never()).save(ArgumentMatchers.any(User.class));
    }


}
