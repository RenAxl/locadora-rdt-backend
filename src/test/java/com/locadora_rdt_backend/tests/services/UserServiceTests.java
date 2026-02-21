package com.locadora_rdt_backend.tests.services;

import java.util.List;
import java.util.Optional;

import com.locadora_rdt_backend.dto.UserUpdateDTO;
import com.locadora_rdt_backend.entities.Role;
import com.locadora_rdt_backend.entities.User;
import com.locadora_rdt_backend.repositories.PasswordResetTokenRepository;
import com.locadora_rdt_backend.repositories.RoleRepository;
import com.locadora_rdt_backend.repositories.UserRepository;
import com.locadora_rdt_backend.services.UserService;
import com.locadora_rdt_backend.services.email.EmailService;
import com.locadora_rdt_backend.services.exceptions.ResourceNotFoundException;
import com.locadora_rdt_backend.tests.factory.UserFactory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(SpringExtension.class)
public class UserServiceTests {

    @InjectMocks
    private UserService service;

    @Mock
    private UserRepository repository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User user;
    private PageImpl<User> page;
    private Role role;

    @BeforeEach
    void setUp() throws Exception {

        user = UserFactory.createUser();
        page = new PageImpl<>(List.of(user));
        role = UserFactory.createRole();

        ReflectionTestUtils.setField(service, "frontendBaseUrl", "http://localhost:4200");
        ReflectionTestUtils.setField(service, "tokenMinutes", 30L);

        Mockito.when(repository.find(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
                .thenReturn(page);

        Mockito.when(repository.save(ArgumentMatchers.any(User.class)))
                .thenReturn(user);

        Mockito.when(roleRepository.getOne(ArgumentMatchers.anyLong()))
                .thenReturn(role);

        Mockito.when(tokenRepository.save(ArgumentMatchers.any()))
                .thenAnswer(inv -> inv.getArgument(0));

        Mockito.doNothing().when(emailService)
                .sendHtmlEmail(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    public void findAllPagedShouldReturnPage() {

        String name = "Renan";
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<com.locadora_rdt_backend.dto.UserDTO> result = service.findAllPaged(name, pageRequest);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals(user.getId(), result.getContent().get(0).getId());

        Mockito.verify(repository, Mockito.times(1)).find(name, pageRequest);
    }

    @Test
    public void insertShouldReturnUserDTO() {

        com.locadora_rdt_backend.dto.UserInsertDTO dto = UserFactory.createUserInsertDTO();

        com.locadora_rdt_backend.dto.UserDTO result = service.insert(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(user.getId(), result.getId());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(repository, Mockito.times(1)).save(captor.capture());

        User savedEntity = captor.getValue();

        Assertions.assertEquals(dto.getName(), savedEntity.getName());
        Assertions.assertEquals(dto.getEmail(), savedEntity.getEmail());
        Assertions.assertEquals(dto.getTelephone(), savedEntity.getTelephone());
        Assertions.assertEquals(dto.getAddress(), savedEntity.getAddress());

        Assertions.assertNull(savedEntity.getPassword(), "insert() seta password como null");
        Assertions.assertFalse(savedEntity.isActive(), "insert() força active=false");

        Assertions.assertNotNull(savedEntity.getRoles());
        Assertions.assertEquals(dto.getRoles().size(), savedEntity.getRoles().size());
        Assertions.assertTrue(savedEntity.getRoles().stream().anyMatch(r -> r.getId().equals(1L)));

        Mockito.verify(roleRepository, Mockito.atLeastOnce()).getOne(1L);

        Mockito.verify(tokenRepository, Mockito.times(1)).save(ArgumentMatchers.any());
        Mockito.verify(emailService, Mockito.times(1))
                .sendHtmlEmail(ArgumentMatchers.eq(dto.getEmail()),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString());
    }

    @Test
    public void findByIdShouldReturnUserDTOWhenIdExists() {

        Long existingId = 1L;

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(user));

        com.locadora_rdt_backend.dto.UserDTO result = service.findById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(user.getId(), result.getId());

        Mockito.verify(repository, Mockito.times(1)).findById(existingId);
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Long nonExistingId = 1000L;

        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(nonExistingId));

        Mockito.verify(repository, Mockito.times(1)).findById(nonExistingId);
    }

    @Test
    public void updateShouldReturnUserDTOWhenIdExists() {

        Long existingId = 1L;

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setName("Novo Nome");
        dto.setEmail("novo@email.com");
        dto.setActive(false);
        dto.setTelephone("31988887777");
        dto.setAddress("Rua B, 456");
        dto.setRoles(List.of(UserFactory.createRoleDTO())); // ESSENCIAL

        Mockito.when(repository.getOne(existingId)).thenReturn(user);
        Mockito.when(repository.save(ArgumentMatchers.any(User.class))).thenReturn(user);

        com.locadora_rdt_backend.dto.UserDTO result = service.update(existingId, dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(user.getId(), result.getId());

        Mockito.verify(repository, Mockito.times(1)).getOne(existingId);
        Mockito.verify(repository, Mockito.times(1)).save(ArgumentMatchers.any(User.class));
        Mockito.verify(roleRepository, Mockito.atLeastOnce()).getOne(1L);
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Long nonExistingId = 1000L;

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setRoles(List.of(UserFactory.createRoleDTO()));

        Mockito.when(repository.getOne(nonExistingId))
                .thenThrow(new javax.persistence.EntityNotFoundException());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(nonExistingId, dto));

        Mockito.verify(repository, Mockito.times(1)).getOne(nonExistingId);
        Mockito.verify(repository, Mockito.never()).save(ArgumentMatchers.any(User.class));
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {

        Long existingId = 1L;

        Mockito.doNothing().when(repository).deleteById(existingId);

        Assertions.assertDoesNotThrow(() -> service.delete(existingId));

        Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Long nonExistingId = 1000L;

        Mockito.doThrow(new org.springframework.dao.EmptyResultDataAccessException(1))
                .when(repository).deleteById(nonExistingId);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistingId));

        Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
    }

    @Test
    public void deleteAllShouldDoNothingAndCallRepositoryDeleteWhenAllIdsExist() {

        List<Long> ids = List.of(1L, 2L, 3L);

        User u1 = UserFactory.createUser(); u1.setId(1L);
        User u2 = UserFactory.createUser(); u2.setId(2L);
        User u3 = UserFactory.createUser(); u3.setId(3L);

        Mockito.when(repository.findAllById(ids)).thenReturn(List.of(u1, u2, u3));
        Mockito.doNothing().when(repository).deleteAllByIds(ids);

        Assertions.assertDoesNotThrow(() -> service.deleteAll(ids));

        Mockito.verify(repository, Mockito.times(1)).findAllById(ids);
        Mockito.verify(repository, Mockito.times(1)).deleteAllByIds(ids);
    }

    @Test
    public void deleteAllShouldThrowIllegalArgumentExceptionWhenIdsIsNull() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.deleteAll(null));

        Mockito.verify(repository, Mockito.never()).findAllById(ArgumentMatchers.anyList());
        Mockito.verify(repository, Mockito.never()).deleteAllByIds(ArgumentMatchers.anyList());
    }

    @Test
    public void deleteAllShouldThrowIllegalArgumentExceptionWhenIdsIsEmpty() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.deleteAll(List.of()));

        Mockito.verify(repository, Mockito.never()).findAllById(ArgumentMatchers.anyList());
        Mockito.verify(repository, Mockito.never()).deleteAllByIds(ArgumentMatchers.anyList());
    }

    @Test
    public void deleteAllShouldThrowResourceNotFoundExceptionWhenAnyIdDoesNotExist() {

        List<Long> ids = List.of(1L, 2L, 3L);

        User u1 = UserFactory.createUser(); u1.setId(1L);
        User u2 = UserFactory.createUser(); u2.setId(2L);

        Mockito.when(repository.findAllById(ids)).thenReturn(List.of(u1, u2));

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.deleteAll(ids));

        Mockito.verify(repository, Mockito.times(1)).findAllById(ids);
        Mockito.verify(repository, Mockito.never()).deleteAllByIds(ArgumentMatchers.anyList());
    }

    @Test
    public void changeActiveStatusShouldDoNothingWhenIdExistsAndUpdateReturnsOne() {

        Long existingId = 1L;
        boolean active = true;

        Mockito.when(repository.updateActiveById(existingId, active)).thenReturn(1);

        Assertions.assertDoesNotThrow(() -> service.changeActiveStatus(existingId, active));

        Mockito.verify(repository, Mockito.times(1)).updateActiveById(existingId, active);
    }

    @Test
    public void changeActiveStatusShouldThrowResourceNotFoundExceptionWhenUpdateReturnsZero() {

        Long nonExistingId = 1000L;
        boolean active = false;

        Mockito.when(repository.updateActiveById(nonExistingId, active)).thenReturn(0);

        ResourceNotFoundException ex = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.changeActiveStatus(nonExistingId, active)
        );

        Assertions.assertTrue(ex.getMessage().contains("Id not found " + nonExistingId));

        Mockito.verify(repository, Mockito.times(1)).updateActiveById(nonExistingId, active);
    }

    @Test
    public void changeActiveStatusShouldThrowRuntimeExceptionWhenRepositoryThrowsDataAccessException() {

        Long existingId = 1L;
        boolean active = true;

        DataIntegrityViolationException dbException =
                new DataIntegrityViolationException("DB error");

        Mockito.when(repository.updateActiveById(existingId, active)).thenThrow(dbException);

        RuntimeException ex = Assertions.assertThrows(
                RuntimeException.class,
                () -> service.changeActiveStatus(existingId, active)
        );

        Assertions.assertEquals("Error changing user status.", ex.getMessage());
        Assertions.assertEquals(dbException, ex.getCause());

        Mockito.verify(repository, Mockito.times(1)).updateActiveById(existingId, active);
    }

    @Test
    public void changeActiveStatusShouldCallRepositoryWithCorrectArguments() {

        Long existingId = 1L;
        boolean active = false;

        Mockito.when(repository.updateActiveById(ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean()))
                .thenReturn(1);

        service.changeActiveStatus(existingId, active);

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Boolean> activeCaptor = ArgumentCaptor.forClass(Boolean.class);

        Mockito.verify(repository).updateActiveById(idCaptor.capture(), activeCaptor.capture());

        Assertions.assertEquals(existingId, idCaptor.getValue());
        Assertions.assertEquals(active, activeCaptor.getValue());
    }

    @Test
    public void getMeShouldReturnUserDTOWhenUserExists() {

        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getName()).thenReturn("renan@teste.com");

        User found = UserFactory.createUser();
        found.setEmail("renan@teste.com");

        Mockito.when(repository.findByEmail("renan@teste.com")).thenReturn(found);

        com.locadora_rdt_backend.dto.UserDTO result = service.getMe(auth);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(found.getId(), result.getId());
        Assertions.assertEquals(found.getEmail(), result.getEmail());

        Mockito.verify(repository, Mockito.times(1)).findByEmail("renan@teste.com");
    }

    @Test
    public void getMeShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExist() {

        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getName()).thenReturn("naoexiste@teste.com");

        Mockito.when(repository.findByEmail("naoexiste@teste.com")).thenReturn(null);

        UsernameNotFoundException ex = Assertions.assertThrows(
                UsernameNotFoundException.class,
                () -> service.getMe(auth)
        );

        Assertions.assertEquals("Usuário não encontrado", ex.getMessage());

        Mockito.verify(repository, Mockito.times(1)).findByEmail("naoexiste@teste.com");
    }

    @Test
    public void getMeShouldThrowNullPointerExceptionWhenAuthenticationIsNull() {

        Authentication auth = null;

        Assertions.assertThrows(NullPointerException.class, () -> service.getMe(auth));

        Mockito.verify(repository, Mockito.never()).findByEmail(ArgumentMatchers.anyString());
    }

    @Test
    public void getMeShouldThrowIllegalArgumentExceptionWhenAuthenticationNameIsNull() {

        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getName()).thenReturn(null);

        Mockito.when(repository.findByEmail(ArgumentMatchers.isNull()))
                .thenThrow(new IllegalArgumentException("Email inválido"));

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.getMe(auth));

        Mockito.verify(repository, Mockito.times(1)).findByEmail(ArgumentMatchers.isNull());
    }
}