package com.locadora_rdt_backend.tests.modules.users.service;

import com.locadora_rdt_backend.common.exception.FileException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.identity.activation.service.AccountActivationService;
import com.locadora_rdt_backend.modules.identity.permissions.model.Permission;
import com.locadora_rdt_backend.modules.identity.roles.model.Role;
import com.locadora_rdt_backend.modules.identity.roles.service.RoleService;
import com.locadora_rdt_backend.modules.identity.users.dto.ChangePasswordDTO;
import com.locadora_rdt_backend.modules.identity.users.dto.UserDTO;
import com.locadora_rdt_backend.modules.identity.users.dto.UserDetailsDTO;
import com.locadora_rdt_backend.modules.identity.users.dto.UserInsertDTO;
import com.locadora_rdt_backend.modules.identity.users.dto.UserMeUpdateDTO;
import com.locadora_rdt_backend.modules.identity.users.dto.UserPhotoDTO;
import com.locadora_rdt_backend.modules.identity.users.dto.UserUpdateDTO;
import com.locadora_rdt_backend.modules.identity.users.mapper.UserMapper;
import com.locadora_rdt_backend.modules.identity.users.model.Address;
import com.locadora_rdt_backend.modules.identity.users.model.User;
import com.locadora_rdt_backend.modules.identity.users.repository.UserRepository;
import com.locadora_rdt_backend.modules.identity.users.service.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("java:S5778")
class UserServiceTests {

    @InjectMocks
    private UserServiceImpl service;

    @Mock
    private UserRepository repository;

    @Mock
    private RoleService roleService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper mapper;

    @Mock
    private AccountActivationService accountActivationService;

    @Mock
    private AuthenticationFacade authenticationFacade;

    @Mock
    private Authentication authentication;

    private Long existingId;
    private Long nonExistingId;
    private User user;
    private UserDTO userDTO;
    private UserDetailsDTO detailsDTO;
    private UserInsertDTO insertDTO;
    private UserUpdateDTO updateDTO;
    private Role role;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 999L;

        role = new Role(1L, "ROLE_ADMIN", "admin", null);

        user = new User();
        user.setId(existingId);
        user.setName("Usuario");
        user.setEmail("usuario@email.com");
        user.setPassword("encoded-password");
        user.setActive(true);
        user.setTelephone("11999999999");
        user.setAddress(createAddress("Rua A", "100"));
        user.getRoles().add(role);

        userDTO = new UserDTO();
        userDTO.setId(existingId);
        userDTO.setName("Usuario");
        userDTO.setEmail("usuario@email.com");
        userDTO.setActive(true);

        detailsDTO = new UserDetailsDTO();
        detailsDTO.setId(existingId);
        detailsDTO.setName("Usuario");
        detailsDTO.setEmail("usuario@email.com");

        insertDTO = new UserInsertDTO();
        insertDTO.setName("Usuario");
        insertDTO.setEmail("usuario@email.com");
        insertDTO.setTelephone("11999999999");
        insertDTO.setAddress(createAddress("Rua A", "100"));
        insertDTO.setRoleIds(List.of(1L));

        updateDTO = new UserUpdateDTO();
        updateDTO.setName("Usuario Atualizado");
        updateDTO.setEmail("usuario@email.com");
        updateDTO.setActive(true);
        updateDTO.setTelephone("11888888888");
        updateDTO.setAddress(createAddress("Rua B", "200"));
        updateDTO.setRoleIds(List.of(1L));
    }

    @Test
    void findAllPagedShouldReturnPage() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        PageImpl<User> page = new PageImpl<>(List.of(user));

        Mockito.when(repository.find("", pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(user)).thenReturn(userDTO);

        Page<UserDTO> result = service.findAllPaged("", pageRequest);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(existingId, result.getContent().get(0).getId());
    }

    @Test
    void findByIdShouldReturnDetailsWhenIdExists() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(user));
        Mockito.when(mapper.toDetailsDTO(user)).thenReturn(detailsDTO);

        UserDetailsDTO result = service.findById(existingId);

        Assertions.assertEquals(existingId, result.getId());
    }

    @Test
    void findByIdShouldThrowWhenIdDoesNotExist() {
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(nonExistingId));
    }

    @Test
    void insertShouldSaveUserWithRolesAndSendActivationEmail() {
        User newUser = new User();

        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(newUser);
        Mockito.when(roleService.findEntityById(1L)).thenReturn(role);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(newUser)).thenReturn(user);
        Mockito.when(mapper.toDTO(user)).thenReturn(userDTO);

        UserDTO result = service.insert(insertDTO);

        Assertions.assertEquals(existingId, result.getId());
        Assertions.assertFalse(newUser.getActive());
        Assertions.assertNull(newUser.getPassword());
        Assertions.assertEquals("admin", newUser.getCreatedBy());
        Assertions.assertTrue(newUser.getRoles().contains(role));
        Mockito.verify(accountActivationService).createActivationTokenAndSendEmail(user);
    }

    @Test
    void updateShouldReturnDTOWhenIdExists() {
        Mockito.when(repository.getOne(existingId)).thenReturn(user);
        Mockito.when(roleService.findEntityById(1L)).thenReturn(role);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(user)).thenReturn(user);
        Mockito.when(mapper.toDTO(user)).thenReturn(userDTO);

        UserDTO result = service.update(existingId, updateDTO);

        Assertions.assertEquals(existingId, result.getId());
        Assertions.assertEquals("admin", user.getUpdatedBy());
        Mockito.verify(mapper).updateEntity(user, updateDTO);
    }

    @Test
    void updateShouldThrowWhenIdDoesNotExist() {
        Mockito.when(repository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(nonExistingId, updateDTO));
    }

    @Test
    void deleteShouldDeleteWhenIdExists() {
        service.delete(existingId);

        Mockito.verify(repository).deleteById(existingId);
    }

    @Test
    void deleteShouldThrowWhenIdDoesNotExist() {
        Mockito.doThrow(new EmptyResultDataAccessException(1)).when(repository).deleteById(nonExistingId);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistingId));
    }

    @Test
    void deleteAllShouldValidateIds() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.deleteAll(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.deleteAll(List.of()));

        Mockito.when(repository.findAllById(List.of(existingId, nonExistingId))).thenReturn(List.of(user));

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> service.deleteAll(List.of(existingId, nonExistingId)));
    }

    @Test
    void deleteAllShouldDeleteExistingIds() {
        Mockito.when(repository.findAllById(List.of(existingId))).thenReturn(List.of(user));

        service.deleteAll(List.of(existingId));

        Mockito.verify(repository).deleteAllByIds(List.of(existingId));
    }

    @Test
    void changeActiveStatusShouldUpdateWhenIdExists() {
        Mockito.when(repository.updateActiveById(existingId, false)).thenReturn(1);

        service.changeActiveStatus(existingId, false);

        Mockito.verify(repository).updateActiveById(existingId, false);
    }

    @Test
    void changeActiveStatusShouldThrowWhenIdDoesNotExist() {
        Mockito.when(repository.updateActiveById(nonExistingId, false)).thenReturn(0);

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> service.changeActiveStatus(nonExistingId, false));
    }

    @Test
    void changeActiveStatusShouldThrowWhenDataAccessFails() {
        Mockito.when(repository.updateActiveById(existingId, false))
                .thenThrow(new DataAccessResourceFailureException("fail"));

        Assertions.assertThrows(RuntimeException.class, () -> service.changeActiveStatus(existingId, false));
    }

    @Test
    void getMeShouldReturnUserWhenAuthenticationIsValid() {
        mockAuthenticatedUser();
        Mockito.when(repository.findByEmail("usuario@email.com")).thenReturn(user);
        Mockito.when(mapper.toDTO(user)).thenReturn(userDTO);

        UserDTO result = service.getMe(authentication);

        Assertions.assertEquals(existingId, result.getId());
    }

    @Test
    void getMeShouldThrowWhenAuthenticationIsInvalid() {
        Assertions.assertThrows(AccessDeniedException.class, () -> service.getMe(null));

        Mockito.when(authentication.isAuthenticated()).thenReturn(false);

        Assertions.assertThrows(AccessDeniedException.class, () -> service.getMe(authentication));
    }

    @Test
    void getMeShouldThrowWhenUserDoesNotExist() {
        mockAuthenticatedUser();
        Mockito.when(repository.findByEmail("usuario@email.com")).thenReturn(null);

        Assertions.assertThrows(AccessDeniedException.class, () -> service.getMe(authentication));
    }

    @Test
    void changePasswordShouldSaveEncodedNewPassword() {
        ChangePasswordDTO dto = new ChangePasswordDTO("senhaAtual", "novaSenha");

        mockAuthenticatedUser();
        Mockito.when(repository.findByEmail("usuario@email.com")).thenReturn(user);
        Mockito.when(passwordEncoder.matches("senhaAtual", "encoded-password")).thenReturn(true);
        Mockito.when(passwordEncoder.matches("novaSenha", "encoded-password")).thenReturn(false);
        Mockito.when(passwordEncoder.encode("novaSenha")).thenReturn("new-encoded-password");

        service.changePassword(authentication, dto);

        Assertions.assertEquals("new-encoded-password", user.getPassword());
        Mockito.verify(repository).save(user);
    }

    @Test
    void changePasswordShouldValidateAuthenticationAndPayload() {
        Assertions.assertThrows(AccessDeniedException.class,
                () -> service.changePassword(null, new ChangePasswordDTO()));

        Mockito.when(authentication.isAuthenticated()).thenReturn(false);

        Assertions.assertThrows(AccessDeniedException.class,
                () -> service.changePassword(authentication, new ChangePasswordDTO()));
    }

    @Test
    void changePasswordShouldThrowWhenPayloadIsNull() {
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.changePassword(authentication, null));
    }

    @Test
    void changePasswordShouldThrowWhenUserDoesNotExist() {
        mockAuthenticatedUser();
        Mockito.when(repository.findByEmail("usuario@email.com")).thenReturn(null);

        Assertions.assertThrows(AccessDeniedException.class,
                () -> service.changePassword(authentication, new ChangePasswordDTO("old", "new")));
    }

    @Test
    void changePasswordShouldThrowWhenCurrentPasswordDoesNotMatch() {
        mockAuthenticatedUser();
        Mockito.when(repository.findByEmail("usuario@email.com")).thenReturn(user);
        Mockito.when(passwordEncoder.matches("senhaAtual", "encoded-password")).thenReturn(false);

        Assertions.assertThrows(AccessDeniedException.class,
                () -> service.changePassword(authentication, new ChangePasswordDTO("senhaAtual", "novaSenha")));
    }

    @Test
    void changePasswordShouldThrowWhenNewPasswordEqualsCurrentPassword() {
        mockAuthenticatedUser();
        Mockito.when(repository.findByEmail("usuario@email.com")).thenReturn(user);
        Mockito.when(passwordEncoder.matches("senhaAtual", "encoded-password")).thenReturn(true);
        Mockito.when(passwordEncoder.matches("senhaAtual", "encoded-password")).thenReturn(true);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.changePassword(authentication, new ChangePasswordDTO("senhaAtual", "senhaAtual")));
    }

    @Test
    void updateMeShouldSaveUserData() {
        UserMeUpdateDTO dto = new UserMeUpdateDTO("Novo Nome", "novo@email.com", "11888888888",
                createAddress("Rua B", "200"));

        mockAuthenticatedUser();
        Mockito.when(repository.findByEmail("usuario@email.com")).thenReturn(user);
        Mockito.when(repository.findByEmail("novo@email.com")).thenReturn(null);
        Mockito.when(repository.save(user)).thenReturn(user);
        Mockito.when(mapper.toDTO(user)).thenReturn(userDTO);

        UserDTO result = service.updateMe(authentication, dto);

        Assertions.assertEquals(existingId, result.getId());
        Assertions.assertEquals("novo@email.com", user.getEmail());
        Assertions.assertEquals("Novo Nome", user.getName());
    }

    @Test
    void updateMeShouldNotCheckDuplicateWhenEmailIsUnchangedIgnoringCase() {
        UserMeUpdateDTO dto = new UserMeUpdateDTO("Novo Nome", "USUARIO@email.com", "11888888888",
                createAddress("Rua B", "200"));

        mockAuthenticatedUser();
        Mockito.when(repository.findByEmail("usuario@email.com")).thenReturn(user);
        Mockito.when(repository.save(user)).thenReturn(user);
        Mockito.when(mapper.toDTO(user)).thenReturn(userDTO);

        service.updateMe(authentication, dto);

        Mockito.verify(repository, Mockito.never()).findByEmail("USUARIO@email.com");
    }

    @Test
    void updateMeShouldValidateAuthenticationAndPayload() {
        Assertions.assertThrows(AccessDeniedException.class,
                () -> service.updateMe(null, new UserMeUpdateDTO()));

        Mockito.when(authentication.isAuthenticated()).thenReturn(false);

        Assertions.assertThrows(AccessDeniedException.class,
                () -> service.updateMe(authentication, new UserMeUpdateDTO()));
    }

    @Test
    void updateMeShouldThrowWhenPayloadIsNull() {
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.updateMe(authentication, null));
    }

    @Test
    void updateMeShouldThrowWhenUserDoesNotExist() {
        mockAuthenticatedUser();
        Mockito.when(repository.findByEmail("usuario@email.com")).thenReturn(null);

        Assertions.assertThrows(AccessDeniedException.class,
                () -> service.updateMe(authentication, new UserMeUpdateDTO("Nome", "email@email.com", null, null)));
    }

    @Test
    void updateMeShouldThrowWhenEmailBelongsToAnotherUser() {
        UserMeUpdateDTO dto = new UserMeUpdateDTO("Novo Nome", "outro@email.com", "11888888888",
                createAddress("Rua B", "200"));
        User otherUser = new User();
        otherUser.setId(2L);

        mockAuthenticatedUser();
        Mockito.when(repository.findByEmail("usuario@email.com")).thenReturn(user);
        Mockito.when(repository.findByEmail("outro@email.com")).thenReturn(otherUser);

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.updateMe(authentication, dto));
    }

    @Test
    void updateMyPhotoShouldSaveValidPhoto() {
        MockMultipartFile file = new MockMultipartFile("file", "photo.png", "image/png", new byte[]{1});

        mockAuthenticatedUser();
        Mockito.when(repository.findByEmail("usuario@email.com")).thenReturn(user);

        service.updateMyPhoto(authentication, file);

        Assertions.assertArrayEquals(new byte[]{1}, user.getPhoto());
        Assertions.assertEquals("image/png", user.getPhotoContentType());
        Mockito.verify(repository).save(user);
    }

    private Address createAddress(String street, String number) {
        Address address = new Address();
        address.setStreet(street);
        address.setNumber(number);
        address.setCity("Belo Horizonte");
        address.setState("MG");
        return address;
    }

    @Test
    void updateMyPhotoShouldValidateAuthenticationAndUser() {
        Assertions.assertThrows(AccessDeniedException.class,
                () -> service.updateMyPhoto(null, new MockMultipartFile("file", new byte[]{1})));

        mockAuthenticatedUser();
        Mockito.when(repository.findByEmail("usuario@email.com")).thenReturn(null);

        Assertions.assertThrows(AccessDeniedException.class,
                () -> service.updateMyPhoto(authentication, new MockMultipartFile("file", new byte[]{1})));
    }

    @Test
    void updateMyPhotoShouldRejectInvalidFiles() {
        mockAuthenticatedUser();
        Mockito.when(repository.findByEmail("usuario@email.com")).thenReturn(user);

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.updateMyPhoto(authentication, null));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.updateMyPhoto(authentication,
                        new MockMultipartFile("file", "photo.gif", "image/gif", new byte[]{1})));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.updateMyPhoto(authentication,
                        new MockMultipartFile("file", "photo.png", "image/png", new byte[2 * 1024 * 1024 + 1])));
    }

    @Test
    void updateMyPhotoShouldThrowWhenFileReadFails() throws IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);

        mockAuthenticatedUser();
        Mockito.when(repository.findByEmail("usuario@email.com")).thenReturn(user);
        Mockito.when(file.isEmpty()).thenReturn(false);
        Mockito.when(file.getContentType()).thenReturn("image/png");
        Mockito.when(file.getSize()).thenReturn(1L);
        Mockito.when(file.getBytes()).thenThrow(IOException.class);

        Assertions.assertThrows(FileException.class, () -> service.updateMyPhoto(authentication, file));
    }

    @Test
    void getMyPhotoShouldReturnPhotoWhenItExists() {
        user.setPhoto(new byte[]{1});
        user.setPhotoContentType("image/png");

        mockAuthenticatedUser();
        Mockito.when(repository.findByEmail("usuario@email.com")).thenReturn(user);

        UserPhotoDTO result = service.getMyPhoto(authentication);

        Assertions.assertArrayEquals(new byte[]{1}, result.getPhoto());
        Assertions.assertEquals("image/png", result.getContentType());
    }

    @Test
    void getMyPhotoShouldReturnNullWhenUserHasNoPhoto() {
        mockAuthenticatedUser();
        Mockito.when(repository.findByEmail("usuario@email.com")).thenReturn(user);

        Assertions.assertNull(service.getMyPhoto(authentication));
    }

    @Test
    void getMyPhotoShouldValidateAuthenticationAndUser() {
        Assertions.assertThrows(AccessDeniedException.class, () -> service.getMyPhoto(null));

        mockAuthenticatedUser();
        Mockito.when(repository.findByEmail("usuario@email.com")).thenReturn(null);

        Assertions.assertThrows(AccessDeniedException.class, () -> service.getMyPhoto(authentication));
    }

    @Test
    void getUserPhotoByIdShouldReturnPhotoWhenItExists() {
        user.setPhoto(new byte[]{1});
        user.setPhotoContentType("image/png");

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(user));

        UserPhotoDTO result = service.getUserPhotoById(existingId);

        Assertions.assertArrayEquals(new byte[]{1}, result.getPhoto());
        Assertions.assertEquals("image/png", result.getContentType());
    }

    @Test
    void getUserPhotoByIdShouldReturnNullWhenUserHasNoPhoto() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(user));

        Assertions.assertNull(service.getUserPhotoById(existingId));
    }

    @Test
    void getUserPhotoByIdShouldValidateIdAndExistence() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.getUserPhotoById(null));

        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.getUserPhotoById(nonExistingId));
    }

    @Test
    void userDetailsMethodsShouldExposeSecurityStateAndAuthorities() {
        Permission permission = new Permission(1L, "USER_READ", "Users");
        role.getPermissions().add(permission);
        user.setActive(null);

        user.prePersist();
        user.preUpdate();

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        Assertions.assertTrue(user.isActive());
        Assertions.assertEquals("usuario@email.com", user.getUsername());
        Assertions.assertTrue(user.isAccountNonExpired());
        Assertions.assertTrue(user.isAccountNonLocked());
        Assertions.assertTrue(user.isCredentialsNonExpired());
        Assertions.assertTrue(user.isEnabled());
        Assertions.assertNotNull(user.getCreatedAt());
        Assertions.assertNotNull(user.getUpdatedAt());
        Assertions.assertTrue(authorities.stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority())));
        Assertions.assertTrue(authorities.stream().anyMatch(a -> "USER_READ".equals(a.getAuthority())));
    }

    private void mockAuthenticatedUser() {
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        Mockito.when(authentication.getName()).thenReturn("usuario@email.com");
    }
}
