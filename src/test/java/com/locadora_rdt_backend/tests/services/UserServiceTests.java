package com.locadora_rdt_backend.tests.services;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.locadora_rdt_backend.dto.*;
import com.locadora_rdt_backend.entities.PasswordResetToken;
import com.locadora_rdt_backend.entities.Role;
import com.locadora_rdt_backend.entities.User;
import com.locadora_rdt_backend.entities.enums.TokenType;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

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

    @Test
    public void changePasswordShouldUpdateAndSaveWhenValidData() {

        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getName()).thenReturn("renan@teste.com");

        User found = UserFactory.createUser();
        found.setEmail("renan@teste.com");
        found.setPassword("ENC_CURRENT");

        ChangePasswordDTO dto = new ChangePasswordDTO("current123", "new123456");

        Mockito.when(repository.findByEmail("renan@teste.com")).thenReturn(found);

        Mockito.when(passwordEncoder.matches("current123", "ENC_CURRENT")).thenReturn(true);

        Mockito.when(passwordEncoder.matches("new123456", "ENC_CURRENT")).thenReturn(false);

        Mockito.when(passwordEncoder.encode("new123456")).thenReturn("ENC_NEW");

        Mockito.when(repository.save(ArgumentMatchers.any(User.class))).thenReturn(found);

        Assertions.assertDoesNotThrow(() -> service.changePassword(auth, dto));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(repository, Mockito.times(1)).save(captor.capture());

        User saved = captor.getValue();
        Assertions.assertEquals("ENC_NEW", saved.getPassword());

        Mockito.verify(repository, Mockito.times(1)).findByEmail("renan@teste.com");
        Mockito.verify(passwordEncoder, Mockito.times(1)).matches("current123", "ENC_CURRENT");
        Mockito.verify(passwordEncoder, Mockito.times(1)).matches("new123456", "ENC_CURRENT");
        Mockito.verify(passwordEncoder, Mockito.times(1)).encode("new123456");
    }

    @Test
    public void changePasswordShouldThrowNullPointerExceptionWhenAuthenticationIsNull() {

        ChangePasswordDTO dto = new ChangePasswordDTO("current123", "new123456");

        NullPointerException ex = Assertions.assertThrows(
                NullPointerException.class,
                () -> service.changePassword(null, dto)
        );

        Assertions.assertEquals("authentication is null", ex.getMessage());

        Mockito.verify(repository, Mockito.never()).findByEmail(ArgumentMatchers.anyString());
        Mockito.verify(repository, Mockito.never()).save(ArgumentMatchers.any(User.class));
        Mockito.verify(passwordEncoder, Mockito.never()).matches(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.verify(passwordEncoder, Mockito.never()).encode(ArgumentMatchers.anyString());
    }

    @Test
    public void changePasswordShouldThrowUsernameNotFoundExceptionWhenUserNotFound() {

        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getName()).thenReturn("naoexiste@teste.com");

        Mockito.when(repository.findByEmail("naoexiste@teste.com")).thenReturn(null);

        ChangePasswordDTO dto = new ChangePasswordDTO("current123", "new123456");

        UsernameNotFoundException ex = Assertions.assertThrows(
                UsernameNotFoundException.class,
                () -> service.changePassword(auth, dto)
        );

        Assertions.assertEquals("Usuário não encontrado", ex.getMessage());

        Mockito.verify(repository, Mockito.times(1)).findByEmail("naoexiste@teste.com");
        Mockito.verify(repository, Mockito.never()).save(ArgumentMatchers.any(User.class));
        Mockito.verify(passwordEncoder, Mockito.never()).matches(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.verify(passwordEncoder, Mockito.never()).encode(ArgumentMatchers.anyString());
    }

    @Test
    public void changePasswordShouldThrowIllegalArgumentExceptionWhenDtoIsNull() {

        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getName()).thenReturn("renan@teste.com");

        User found = UserFactory.createUser();
        found.setEmail("renan@teste.com");
        found.setPassword("ENC_CURRENT");

        Mockito.when(repository.findByEmail("renan@teste.com")).thenReturn(found);

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.changePassword(auth, null)
        );

        Assertions.assertEquals("Dados inválidos", ex.getMessage());

        Mockito.verify(repository, Mockito.times(1)).findByEmail("renan@teste.com");
        Mockito.verify(repository, Mockito.never()).save(ArgumentMatchers.any(User.class));
        Mockito.verify(passwordEncoder, Mockito.never()).matches(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.verify(passwordEncoder, Mockito.never()).encode(ArgumentMatchers.anyString());
    }

    @Test
    public void changePasswordShouldThrowIllegalArgumentExceptionWhenCurrentPasswordDoesNotMatch() {

        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getName()).thenReturn("renan@teste.com");

        User found = UserFactory.createUser();
        found.setEmail("renan@teste.com");
        found.setPassword("ENC_CURRENT");

        Mockito.when(repository.findByEmail("renan@teste.com")).thenReturn(found);

        ChangePasswordDTO dto = new ChangePasswordDTO("wrongCurrent", "new123456");

        Mockito.when(passwordEncoder.matches("wrongCurrent", "ENC_CURRENT")).thenReturn(false);

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.changePassword(auth, dto)
        );

        Assertions.assertEquals("Senha atual incorreta", ex.getMessage());

        Mockito.verify(repository, Mockito.times(1)).findByEmail("renan@teste.com");
        Mockito.verify(passwordEncoder, Mockito.times(1)).matches("wrongCurrent", "ENC_CURRENT");
        Mockito.verify(passwordEncoder, Mockito.never()).encode(ArgumentMatchers.anyString());
        Mockito.verify(repository, Mockito.never()).save(ArgumentMatchers.any(User.class));
    }

    @Test
    public void changePasswordShouldThrowIllegalArgumentExceptionWhenNewPasswordIsSameAsCurrent() {

        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getName()).thenReturn("renan@teste.com");

        User found = UserFactory.createUser();
        found.setEmail("renan@teste.com");
        found.setPassword("ENC_CURRENT");

        Mockito.when(repository.findByEmail("renan@teste.com")).thenReturn(found);

        ChangePasswordDTO dto = new ChangePasswordDTO("current123", "current123");

        Mockito.when(passwordEncoder.matches("current123", "ENC_CURRENT")).thenReturn(true);
        Mockito.when(passwordEncoder.matches("current123", "ENC_CURRENT")).thenReturn(true);

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.changePassword(auth, dto)
        );

        Assertions.assertEquals("A nova senha não pode ser igual à senha atual", ex.getMessage());

        Mockito.verify(repository, Mockito.times(1)).findByEmail("renan@teste.com");
        Mockito.verify(passwordEncoder, Mockito.times(2)).matches("current123", "ENC_CURRENT");
        Mockito.verify(passwordEncoder, Mockito.never()).encode(ArgumentMatchers.anyString());
        Mockito.verify(repository, Mockito.never()).save(ArgumentMatchers.any(User.class));
    }

    @Test
    public void changePasswordShouldPropagateRuntimeExceptionWhenRepositorySaveThrows() {

        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getName()).thenReturn("renan@teste.com");

        User found = UserFactory.createUser();
        found.setEmail("renan@teste.com");
        found.setPassword("ENC_CURRENT");

        ChangePasswordDTO dto = new ChangePasswordDTO("current123", "new123456");

        Mockito.when(repository.findByEmail("renan@teste.com")).thenReturn(found);

        Mockito.when(passwordEncoder.matches("current123", "ENC_CURRENT")).thenReturn(true);
        Mockito.when(passwordEncoder.matches("new123456", "ENC_CURRENT")).thenReturn(false);
        Mockito.when(passwordEncoder.encode("new123456")).thenReturn("ENC_NEW");

        Mockito.when(repository.save(ArgumentMatchers.any(User.class)))
                .thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = Assertions.assertThrows(
                RuntimeException.class,
                () -> service.changePassword(auth, dto)
        );

        Assertions.assertEquals("DB error", ex.getMessage());

        Mockito.verify(repository, Mockito.times(1)).findByEmail("renan@teste.com");
        Mockito.verify(passwordEncoder, Mockito.times(1)).encode("new123456");
        Mockito.verify(repository, Mockito.times(1)).save(ArgumentMatchers.any(User.class));
    }

    @Test
    public void changePasswordShouldCallRepositoryFindByEmailWithAuthenticationName() {

        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getName()).thenReturn("renan@teste.com");

        User found = UserFactory.createUser();
        found.setEmail("renan@teste.com");
        found.setPassword("ENC_CURRENT");

        ChangePasswordDTO dto = new ChangePasswordDTO("current123", "new123456");

        Mockito.when(repository.findByEmail(ArgumentMatchers.anyString())).thenReturn(found);

        Mockito.when(passwordEncoder.matches("current123", "ENC_CURRENT")).thenReturn(true);
        Mockito.when(passwordEncoder.matches("new123456", "ENC_CURRENT")).thenReturn(false);
        Mockito.when(passwordEncoder.encode("new123456")).thenReturn("ENC_NEW");

        Mockito.when(repository.save(ArgumentMatchers.any(User.class))).thenReturn(found);

        service.changePassword(auth, dto);

        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(repository).findByEmail(emailCaptor.capture());

        Assertions.assertEquals("renan@teste.com", emailCaptor.getValue());
    }

    @Test
    public void updateMeShouldUpdateAndSaveWhenValidDataAndEmailChanges() {

        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getName()).thenReturn("old@teste.com");

        User loggedUser = UserFactory.createUser();
        loggedUser.setId(1L);
        loggedUser.setEmail("old@teste.com");

        UserMeUpdateDTO dto = new UserMeUpdateDTO();
        dto.setName("Novo Nome");
        dto.setEmail("new@teste.com");
        dto.setTelephone("31988887777");
        dto.setAddress("Rua Nova, 123");

        Mockito.when(repository.findByEmail("old@teste.com")).thenReturn(loggedUser);
        Mockito.when(repository.findByEmail("new@teste.com")).thenReturn(null);
        Mockito.when(repository.save(ArgumentMatchers.any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDTO result = service.updateMe(auth, dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals("Novo Nome", result.getName());
        Assertions.assertEquals("new@teste.com", result.getEmail());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(repository, Mockito.times(1)).save(captor.capture());

        User saved = captor.getValue();
        Assertions.assertEquals("Novo Nome", saved.getName());
        Assertions.assertEquals("new@teste.com", saved.getEmail());
        Assertions.assertEquals("31988887777", saved.getTelephone());
        Assertions.assertEquals("Rua Nova, 123", saved.getAddress());

        Mockito.verify(repository, Mockito.times(1)).findByEmail("old@teste.com");
        Mockito.verify(repository, Mockito.times(1)).findByEmail("new@teste.com");
    }

    @Test
    public void updateMeShouldThrowNullPointerExceptionWhenAuthenticationIsNull() {

        UserMeUpdateDTO dto = new UserMeUpdateDTO();
        dto.setName("Novo Nome");
        dto.setEmail("new@teste.com");

        NullPointerException ex = Assertions.assertThrows(
                NullPointerException.class,
                () -> service.updateMe(null, dto)
        );

        Assertions.assertEquals("authentication is null", ex.getMessage());

        Mockito.verify(repository, Mockito.never()).findByEmail(ArgumentMatchers.anyString());
        Mockito.verify(repository, Mockito.never()).save(ArgumentMatchers.any(User.class));
    }

    @Test
    public void updateMeShouldThrowIllegalArgumentExceptionWhenNewEmailAlreadyInUseByAnotherUser() {

        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getName()).thenReturn("old@teste.com");

        User loggedUser = UserFactory.createUser();
        loggedUser.setId(1L);
        loggedUser.setEmail("old@teste.com");

        UserMeUpdateDTO dto = new UserMeUpdateDTO();
        dto.setName("Novo Nome");
        dto.setEmail("taken@teste.com");
        dto.setTelephone("31988887777");
        dto.setAddress("Rua Nova, 123");

        Mockito.when(repository.findByEmail("old@teste.com")).thenReturn(loggedUser);

        User otherUser = UserFactory.createUser();
        otherUser.setId(2L);
        otherUser.setEmail("taken@teste.com");

        Mockito.when(repository.findByEmail("taken@teste.com")).thenReturn(otherUser);

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.updateMe(auth, dto)
        );

        Assertions.assertEquals("Email já está em uso", ex.getMessage());

        Mockito.verify(repository, Mockito.times(1)).findByEmail("old@teste.com");
        Mockito.verify(repository, Mockito.times(1)).findByEmail("taken@teste.com");
        Mockito.verify(repository, Mockito.never()).save(ArgumentMatchers.any(User.class));
    }

    @Test
    public void updateMyPhotoShouldUpdateAndSaveWhenValidData() throws Exception {

        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getName()).thenReturn("renan@teste.com");

        byte[] bytes = "fake-image-bytes".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.png",
                "image/png",
                bytes
        );

        User found = UserFactory.createUser();
        found.setEmail("renan@teste.com");

        Mockito.when(repository.findByEmail("renan@teste.com")).thenReturn(found);
        Mockito.when(repository.save(ArgumentMatchers.any(User.class))).thenReturn(found);

        Assertions.assertDoesNotThrow(() -> service.updateMyPhoto(auth, file));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(repository).save(captor.capture());

        User saved = captor.getValue();
        Assertions.assertArrayEquals(bytes, saved.getPhoto());
        Assertions.assertEquals("image/png", saved.getPhotoContentType());

        Mockito.verify(repository, Mockito.times(1)).findByEmail("renan@teste.com");
    }

    @Test
    public void updateMyPhotoShouldThrowUsernameNotFoundExceptionWhenUserNotFound() {

        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getName()).thenReturn("naoexiste@teste.com");

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.png",
                "image/png",
                "bytes".getBytes()
        );

        Mockito.when(repository.findByEmail("naoexiste@teste.com")).thenReturn(null);

        UsernameNotFoundException ex = Assertions.assertThrows(
                UsernameNotFoundException.class,
                () -> service.updateMyPhoto(auth, file)
        );

        Assertions.assertEquals("Usuário não encontrado", ex.getMessage());

        Mockito.verify(repository, Mockito.times(1)).findByEmail("naoexiste@teste.com");
        Mockito.verify(repository, Mockito.never()).save(ArgumentMatchers.any());
    }

    @Test
    public void getMyPhotoShouldReturnPhotoDTOWhenUserAndPhotoExist() {

        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getName()).thenReturn("renan@teste.com");

        byte[] bytes = "image-bytes".getBytes();

        User found = UserFactory.createUser();
        found.setEmail("renan@teste.com");
        found.setPhoto(bytes);
        found.setPhotoContentType("image/jpeg");

        Mockito.when(repository.findByEmail("renan@teste.com")).thenReturn(found);

        UserPhotoDTO result = service.getMyPhoto(auth);

        Assertions.assertNotNull(result);
        Assertions.assertArrayEquals(bytes, result.getPhoto());
        Assertions.assertEquals("image/jpeg", result.getContentType());

        Mockito.verify(repository, Mockito.times(1)).findByEmail("renan@teste.com");
    }

    @Test
    public void requestPasswordResetShouldDoNothingWhenEmailNotFound() {

        ForgotPasswordDTO dto = new ForgotPasswordDTO();
        dto.setEmail("naoexiste@teste.com");

        Mockito.when(repository.findByEmail("naoexiste@teste.com")).thenReturn(null);

        Assertions.assertDoesNotThrow(() -> service.requestPasswordReset(dto));

        Mockito.verify(repository, Mockito.times(1)).findByEmail("naoexiste@teste.com");
        Mockito.verify(tokenRepository, Mockito.never()).deleteByUserIdAndType(Mockito.anyLong(), Mockito.any());
        Mockito.verify(tokenRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(emailService, Mockito.never()).sendHtmlEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void requestPasswordResetShouldDoNothingWhenUserIsInactive() {

        ForgotPasswordDTO dto = new ForgotPasswordDTO();
        dto.setEmail("inativo@teste.com");

        User inactiveUser = UserFactory.createUser();
        inactiveUser.setId(10L);
        inactiveUser.setEmail("inativo@teste.com");
        inactiveUser.setActive(false);

        Mockito.when(repository.findByEmail("inativo@teste.com")).thenReturn(inactiveUser);

        Assertions.assertDoesNotThrow(() -> service.requestPasswordReset(dto));

        Mockito.verify(repository, Mockito.times(1)).findByEmail("inativo@teste.com");
        Mockito.verify(tokenRepository, Mockito.never()).deleteByUserIdAndType(Mockito.anyLong(), Mockito.any());
        Mockito.verify(tokenRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(emailService, Mockito.never()).sendHtmlEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void requestPasswordResetShouldCreateTokenAndSendEmailWhenUserIsActive() {

        ForgotPasswordDTO dto = new ForgotPasswordDTO();
        dto.setEmail("ativo@teste.com");

        User activeUser = UserFactory.createUser();
        activeUser.setId(1L);
        activeUser.setName("Renan");
        activeUser.setEmail("ativo@teste.com");
        activeUser.setActive(true);

        Mockito.when(repository.findByEmail("ativo@teste.com")).thenReturn(activeUser);

        Assertions.assertDoesNotThrow(() -> service.requestPasswordReset(dto));

        Mockito.verify(tokenRepository, Mockito.times(1))
                .deleteByUserIdAndType(1L, TokenType.PASSWORD_RESET);

        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        Mockito.verify(tokenRepository, Mockito.times(1)).save(tokenCaptor.capture());

        PasswordResetToken savedToken = tokenCaptor.getValue();
        Assertions.assertNotNull(savedToken.getToken());
        Assertions.assertFalse(savedToken.getToken().isBlank());
        Assertions.assertEquals(TokenType.PASSWORD_RESET, savedToken.getType());
        Assertions.assertEquals(activeUser, savedToken.getUser());

        Assertions.assertNotNull(savedToken.getExpiration());
        Assertions.assertTrue(savedToken.getExpiration().isAfter(Instant.now()));

        ArgumentCaptor<String> toCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> htmlCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(emailService, Mockito.times(1))
                .sendHtmlEmail(toCaptor.capture(), subjectCaptor.capture(), htmlCaptor.capture());

        Assertions.assertEquals("ativo@teste.com", toCaptor.getValue());
        Assertions.assertTrue(subjectCaptor.getValue().contains("Recuperação de senha"));

        String html = htmlCaptor.getValue();
        Assertions.assertTrue(html.contains("/auth/reset"));
        Assertions.assertTrue(html.contains("token=" + savedToken.getToken()));
    }

    @Test
    public void resetPasswordShouldThrowIllegalArgumentExceptionWhenTokenIsBlank() {

        String token = "   ";
        NewPasswordDTO dto = new NewPasswordDTO("newPass123");

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.resetPassword(token, dto)
        );

        Assertions.assertEquals("Token inválido", ex.getMessage());

        Mockito.verify(tokenRepository, Mockito.never())
                .findByTokenAndTypeAndExpirationAfter(Mockito.anyString(), Mockito.any(), Mockito.any());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(User.class));
        Mockito.verify(tokenRepository, Mockito.never()).delete(Mockito.any());
    }

    @Test
    public void resetPasswordShouldThrowRuntimeExceptionWhenTokenIsInvalidOrExpired() {

        String token = "invalid-token";
        NewPasswordDTO dto = new NewPasswordDTO("newPass123");

        Mockito.when(tokenRepository.findByTokenAndTypeAndExpirationAfter(
                ArgumentMatchers.eq(token),
                ArgumentMatchers.eq(TokenType.PASSWORD_RESET),
                ArgumentMatchers.any(Instant.class)
        )).thenReturn(Optional.empty());

        RuntimeException ex = Assertions.assertThrows(
                RuntimeException.class,
                () -> service.resetPassword(token, dto)
        );

        Assertions.assertEquals("Token inválido ou expirado", ex.getMessage());

        Mockito.verify(repository, Mockito.never()).save(Mockito.any(User.class));
        Mockito.verify(tokenRepository, Mockito.never()).delete(Mockito.any());
        Mockito.verify(passwordEncoder, Mockito.never()).encode(Mockito.anyString());
    }

    @Test
    public void resetPasswordShouldThrowIllegalArgumentExceptionWhenNewPasswordEqualsCurrentPassword() {

        String token = "valid-token";
        NewPasswordDTO dto = new NewPasswordDTO("samePass123");

        User u = UserFactory.createUser();
        u.setId(1L);
        u.setPassword("ENC_CURRENT");

        PasswordResetToken prt = new PasswordResetToken();
        prt.setToken(token);
        prt.setType(TokenType.PASSWORD_RESET);
        prt.setUser(u);
        prt.setExpiration(Instant.now().plusSeconds(60));

        Mockito.when(tokenRepository.findByTokenAndTypeAndExpirationAfter(
                ArgumentMatchers.eq(token),
                ArgumentMatchers.eq(TokenType.PASSWORD_RESET),
                ArgumentMatchers.any(Instant.class)
        )).thenReturn(Optional.of(prt));

        Mockito.when(passwordEncoder.matches("samePass123", "ENC_CURRENT")).thenReturn(true);

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.resetPassword(token, dto)
        );

        Assertions.assertEquals("A nova senha não pode ser igual à senha atual", ex.getMessage());

        Mockito.verify(repository, Mockito.never()).save(Mockito.any(User.class));
        Mockito.verify(tokenRepository, Mockito.never()).delete(Mockito.any());
        Mockito.verify(passwordEncoder, Mockito.never()).encode(Mockito.anyString());
    }

}