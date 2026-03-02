package com.locadora_rdt_backend.tests.modules.users.controller;

import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.identity.passwordreset.dto.ForgotPasswordDTO;
import com.locadora_rdt_backend.modules.identity.passwordreset.dto.NewPasswordDTO;
import com.locadora_rdt_backend.modules.users.dto.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.locadora_rdt_backend.modules.users.controller.UserController;
import com.locadora_rdt_backend.modules.users.service.UserService;
import com.locadora_rdt_backend.tests.factories.UserFactory;

import java.net.URI;
import java.util.List;

@ExtendWith(SpringExtension.class)
public class UserControllerTests {

    @InjectMocks
    private UserController controller;

    @Mock
    private UserService service;

    private UserDTO userDTO;
    private PageImpl<UserDTO> page;

    @BeforeEach
    void setUp() throws Exception {

        userDTO = UserFactory.createUserDTO();
        page = new PageImpl<>(List.of(userDTO));

        Mockito.when(service.findAllPaged(ArgumentMatchers.anyString(), ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(page);

        Mockito.when(service.insert(ArgumentMatchers.any(UserInsertDTO.class)))
                .thenReturn(userDTO);
    }

    @Test
    public void findAllPagedShouldReturnResponseEntityWithPage() {

        String name = " Renan "; // com espaços pra validar o trim()
        Integer pageNumber = 0;
        Integer linesPerPage = 3;
        String direction = "ASC";
        String orderBy = "name";

        ResponseEntity<Page<UserDTO>> response = controller.findAllPaged(
                name, pageNumber, linesPerPage, direction, orderBy
        );

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertFalse(response.getBody().isEmpty());

        PageRequest expectedPageRequest = PageRequest.of(pageNumber, linesPerPage, Sort.Direction.valueOf(direction), orderBy);
        Mockito.verify(service, Mockito.times(1)).findAllPaged(name.trim(), expectedPageRequest);
    }

    @Test
    public void insertShouldReturnCreatedAndLocationHeader() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/users");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        UserInsertDTO dto = UserFactory.createUserInsertDTO();

        ResponseEntity<UserDTO> response = controller.insert(dto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(201, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(userDTO.getId(), response.getBody().getId());

        URI location = response.getHeaders().getLocation();
        Assertions.assertNotNull(location);
        Assertions.assertTrue(location.toString().endsWith("/users/" + userDTO.getId()));

        Mockito.verify(service, Mockito.times(1)).insert(dto);
    }

    @Test
    public void findByIdShouldReturnResponseEntityWithUserDTOWhenIdExists() {

        Long existingId = 1L;

        Mockito.when(service.findById(existingId)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = controller.findById(existingId);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(userDTO.getId(), response.getBody().getId());

        Mockito.verify(service, Mockito.times(1)).findById(existingId);
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Long nonExistingId = 1000L;

        Mockito.when(service.findById(nonExistingId))
                .thenThrow(new ResourceNotFoundException("Entity not found"));

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            controller.findById(nonExistingId);
        });

        Mockito.verify(service, Mockito.times(1)).findById(nonExistingId);
    }

    @Test
    public void updateShouldReturnResponseEntityWithUserDTOWhenIdExists() {

        Long existingId = 1L;

        UserUpdateDTO dto = new UserUpdateDTO();

        Mockito.when(service.update(existingId, dto)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = controller.update(existingId, dto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(userDTO.getId(), response.getBody().getId());

        Mockito.verify(service, Mockito.times(1)).update(existingId, dto);
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Long nonExistingId = 1000L;

        UserUpdateDTO dto = new UserUpdateDTO();

        Mockito.when(service.update(nonExistingId, dto))
                .thenThrow(new ResourceNotFoundException("Id not found " + nonExistingId));

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            controller.update(nonExistingId, dto);
        });

        Mockito.verify(service, Mockito.times(1)).update(nonExistingId, dto);
    }

    @Test
    public void deleteShouldReturnNoContentWhenIdExists() {

        Long existingId = 1L;

        Mockito.doNothing().when(service).delete(existingId);

        ResponseEntity<UserDTO> response = controller.delete(existingId);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(204, response.getStatusCodeValue());
        Assertions.assertNull(response.getBody());

        Mockito.verify(service, Mockito.times(1)).delete(existingId);
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Long nonExistingId = 1000L;

        Mockito.doThrow(new ResourceNotFoundException("Id not found " + nonExistingId))
                .when(service).delete(nonExistingId);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            controller.delete(nonExistingId);
        });

        Mockito.verify(service, Mockito.times(1)).delete(nonExistingId);
    }

    @Test
    public void deleteAllShouldReturnNoContentAndCallServiceDeleteAll() {

        List<Long> ids = List.of(1L, 2L, 3L);

        Mockito.doNothing().when(service).deleteAll(ids);

        ResponseEntity<Void> response = controller.deleteAll(ids);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(204, response.getStatusCodeValue());
        Assertions.assertNull(response.getBody());

        Mockito.verify(service, Mockito.times(1)).deleteAll(ids);
    }

    @Test
    public void deleteAllShouldThrowIllegalArgumentExceptionWhenIdsIsNull() {

        Mockito.doThrow(new IllegalArgumentException("Lista de ids vazia"))
                .when(service).deleteAll(null);

        Assertions.assertThrows(IllegalArgumentException.class, () -> controller.deleteAll(null));

        Mockito.verify(service, Mockito.times(1)).deleteAll(null);
    }

    @Test
    public void deleteAllShouldThrowIllegalArgumentExceptionWhenIdsIsEmpty() {

        List<Long> ids = List.of();

        Mockito.doThrow(new IllegalArgumentException("Lista de ids vazia"))
                .when(service).deleteAll(ids);

        Assertions.assertThrows(IllegalArgumentException.class, () -> controller.deleteAll(ids));

        Mockito.verify(service, Mockito.times(1)).deleteAll(ids);
    }

    @Test
    public void deleteAllShouldThrowResourceNotFoundExceptionWhenAnyIdDoesNotExist() {

        List<Long> ids = List.of(1L, 2L, 3L);

        Mockito.doThrow(new ResourceNotFoundException("Um ou mais IDs não existem"))
                .when(service).deleteAll(ids);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> controller.deleteAll(ids));

        Mockito.verify(service, Mockito.times(1)).deleteAll(ids);
    }

    @Test
    public void changeActiveShouldReturnNoContentWhenIdExists() {

        Long existingId = 1L;
        boolean active = true;

        Mockito.doNothing().when(service).changeActiveStatus(existingId, active);

        ResponseEntity<UserDTO> response = controller.changeActive(existingId, active);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(204, response.getStatusCodeValue());
        Assertions.assertNull(response.getBody());

        Mockito.verify(service, Mockito.times(1)).changeActiveStatus(existingId, active);
    }

    @Test
    public void changeActiveShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Long nonExistingId = 1000L;
        boolean active = false;

        Mockito.doThrow(new ResourceNotFoundException("Id not found " + nonExistingId))
                .when(service).changeActiveStatus(nonExistingId, active);

        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> controller.changeActive(nonExistingId, active)
        );

        Mockito.verify(service, Mockito.times(1)).changeActiveStatus(nonExistingId, active);
    }

    @Test
    public void changeActiveShouldThrowRuntimeExceptionWhenServiceThrowsRuntimeException() {

        Long existingId = 1L;
        boolean active = true;

        Mockito.doThrow(new RuntimeException("Error changing user status."))
                .when(service).changeActiveStatus(existingId, active);

        RuntimeException ex = Assertions.assertThrows(
                RuntimeException.class,
                () -> controller.changeActive(existingId, active)
        );

        Assertions.assertEquals("Error changing user status.", ex.getMessage());

        Mockito.verify(service, Mockito.times(1)).changeActiveStatus(existingId, active);
    }

    @Test
    public void changeActiveShouldCallServiceWithCorrectArguments() {

        Long existingId = 1L;
        boolean active = false;

        Mockito.doNothing().when(service).changeActiveStatus(ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean());

        controller.changeActive(existingId, active);

        Mockito.verify(service, Mockito.times(1)).changeActiveStatus(existingId, active);
    }

    @Test
    public void getMeShouldReturnOkAndUserDTOWhenServiceReturnsDTO() {

        Authentication authentication = Mockito.mock(Authentication.class);

        Mockito.when(service.getMe(authentication)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = controller.getMe(authentication);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(userDTO.getId(), response.getBody().getId());

        Mockito.verify(service, Mockito.times(1)).getMe(authentication);
    }

    @Test
    public void getMeShouldThrowUsernameNotFoundExceptionWhenServiceThrowsUsernameNotFoundException() {

        Authentication authentication = Mockito.mock(Authentication.class);

        Mockito.when(service.getMe(authentication))
                .thenThrow(new UsernameNotFoundException("Usuário não encontrado"));

        UsernameNotFoundException ex = Assertions.assertThrows(
                UsernameNotFoundException.class,
                () -> controller.getMe(authentication)
        );

        Assertions.assertEquals("Usuário não encontrado", ex.getMessage());

        Mockito.verify(service, Mockito.times(1)).getMe(authentication);
    }

    @Test
    public void getMeShouldThrowNullPointerExceptionWhenAuthenticationIsNullAndServiceThrowsNPE() {

        Authentication authentication = null;

        Mockito.when(service.getMe(ArgumentMatchers.isNull()))
                .thenThrow(new NullPointerException("authentication is null"));

        NullPointerException ex = Assertions.assertThrows(
                NullPointerException.class,
                () -> controller.getMe(authentication)
        );

        Assertions.assertEquals("authentication is null", ex.getMessage());

        Mockito.verify(service, Mockito.times(1)).getMe(ArgumentMatchers.isNull());
    }

    @Test
    public void getMeShouldThrowRuntimeExceptionWhenServiceThrowsRuntimeException() {

        Authentication authentication = Mockito.mock(Authentication.class);

        Mockito.when(service.getMe(authentication))
                .thenThrow(new RuntimeException("Erro inesperado"));

        RuntimeException ex = Assertions.assertThrows(
                RuntimeException.class,
                () -> controller.getMe(authentication)
        );

        Assertions.assertEquals("Erro inesperado", ex.getMessage());

        Mockito.verify(service, Mockito.times(1)).getMe(authentication);
    }


    @Test
    public void changePasswordShouldReturnNoContentWhenServiceSucceeds() {

        Authentication authentication = Mockito.mock(Authentication.class);

        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setCurrentPassword("current123");
        dto.setNewPassword("new123456");

        Mockito.doNothing().when(service).changePassword(authentication, dto);

        ResponseEntity<Void> response = controller.changePassword(authentication, dto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(204, response.getStatusCodeValue());
        Assertions.assertNull(response.getBody());

        Mockito.verify(service, Mockito.times(1)).changePassword(authentication, dto);
    }

    @Test
    public void changePasswordShouldThrowNullPointerExceptionWhenServiceThrowsNullPointerException() {

        Authentication authentication = null;

        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setCurrentPassword("current123");
        dto.setNewPassword("new123456");

        Mockito.doThrow(new NullPointerException("authentication is null"))
                .when(service).changePassword(ArgumentMatchers.isNull(), ArgumentMatchers.any(ChangePasswordDTO.class));

        NullPointerException ex = Assertions.assertThrows(
                NullPointerException.class,
                () -> controller.changePassword(authentication, dto)
        );

        Assertions.assertEquals("authentication is null", ex.getMessage());

        Mockito.verify(service, Mockito.times(1))
                .changePassword(ArgumentMatchers.isNull(), ArgumentMatchers.any(ChangePasswordDTO.class));
    }

    @Test
    public void changePasswordShouldThrowUsernameNotFoundExceptionWhenServiceThrowsUsernameNotFoundException() {

        Authentication authentication = Mockito.mock(Authentication.class);

        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setCurrentPassword("current123");
        dto.setNewPassword("new123456");

        Mockito.doThrow(new UsernameNotFoundException("Usuário não encontrado"))
                .when(service).changePassword(authentication, dto);

        UsernameNotFoundException ex = Assertions.assertThrows(
                UsernameNotFoundException.class,
                () -> controller.changePassword(authentication, dto)
        );

        Assertions.assertEquals("Usuário não encontrado", ex.getMessage());

        Mockito.verify(service, Mockito.times(1)).changePassword(authentication, dto);
    }

    @Test
    public void changePasswordShouldThrowIllegalArgumentExceptionWhenServiceThrowsSenhaAtualIncorreta() {

        Authentication authentication = Mockito.mock(Authentication.class);

        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setCurrentPassword("wrong");
        dto.setNewPassword("new123456");

        Mockito.doThrow(new IllegalArgumentException("Senha atual incorreta"))
                .when(service).changePassword(authentication, dto);

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> controller.changePassword(authentication, dto)
        );

        Assertions.assertEquals("Senha atual incorreta", ex.getMessage());

        Mockito.verify(service, Mockito.times(1)).changePassword(authentication, dto);
    }

    @Test
    public void changePasswordShouldThrowIllegalArgumentExceptionWhenServiceThrowsNewPasswordEqualsCurrent() {

        Authentication authentication = Mockito.mock(Authentication.class);

        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setCurrentPassword("current123");
        dto.setNewPassword("current123");

        Mockito.doThrow(new IllegalArgumentException("A nova senha não pode ser igual à senha atual"))
                .when(service).changePassword(authentication, dto);

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> controller.changePassword(authentication, dto)
        );

        Assertions.assertEquals("A nova senha não pode ser igual à senha atual", ex.getMessage());

        Mockito.verify(service, Mockito.times(1)).changePassword(authentication, dto);
    }

    @Test
    public void changePasswordShouldThrowIllegalArgumentExceptionWhenServiceThrowsDadosInvalidos() {

        Authentication authentication = Mockito.mock(Authentication.class);

        ChangePasswordDTO dto = null;

        Mockito.doThrow(new IllegalArgumentException("Dados inválidos"))
                .when(service).changePassword(ArgumentMatchers.eq(authentication), ArgumentMatchers.isNull());

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> controller.changePassword(authentication, dto)
        );

        Assertions.assertEquals("Dados inválidos", ex.getMessage());

        Mockito.verify(service, Mockito.times(1))
                .changePassword(ArgumentMatchers.eq(authentication), ArgumentMatchers.isNull());
    }

    @Test
    public void changePasswordShouldThrowRuntimeExceptionWhenServiceThrowsRuntimeException() {

        Authentication authentication = Mockito.mock(Authentication.class);

        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setCurrentPassword("current123");
        dto.setNewPassword("new123456");

        Mockito.doThrow(new RuntimeException("DB error"))
                .when(service).changePassword(authentication, dto);

        RuntimeException ex = Assertions.assertThrows(
                RuntimeException.class,
                () -> controller.changePassword(authentication, dto)
        );

        Assertions.assertEquals("DB error", ex.getMessage());

        Mockito.verify(service, Mockito.times(1)).changePassword(authentication, dto);
    }

    @Test
    public void changePasswordShouldCallServiceWithCorrectArguments() {

        Authentication authentication = Mockito.mock(Authentication.class);

        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setCurrentPassword("current123");
        dto.setNewPassword("new123456");

        Mockito.doNothing().when(service).changePassword(ArgumentMatchers.any(), ArgumentMatchers.any());

        controller.changePassword(authentication, dto);

        Mockito.verify(service, Mockito.times(1)).changePassword(authentication, dto);
    }

    @Test
    public void updateMyPhotoShouldReturnNoContentWhenServiceSucceeds() {

        Authentication authentication = Mockito.mock(Authentication.class);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.png",
                "image/png",
                "bytes".getBytes()
        );

        Mockito.doNothing().when(service).updateMyPhoto(authentication, file);

        ResponseEntity<Void> response = controller.updateMyPhoto(authentication, file);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(204, response.getStatusCodeValue());
        Assertions.assertNull(response.getBody());

        Mockito.verify(service, Mockito.times(1)).updateMyPhoto(authentication, file);
    }

    @Test
    public void getMyPhotoShouldReturnOkWithBytesAndHeadersWhenServiceReturnsValidDTO() {

        Authentication authentication = Mockito.mock(Authentication.class);

        byte[] photoBytes = "image-bytes".getBytes();
        UserPhotoDTO dto = new UserPhotoDTO(photoBytes, "image/png");

        Mockito.when(service.getMyPhoto(authentication)).thenReturn(dto);

        ResponseEntity<byte[]> response = controller.getMyPhoto(authentication);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());

        Assertions.assertNotNull(response.getBody());
        Assertions.assertArrayEquals(photoBytes, response.getBody());

        Assertions.assertEquals(MediaType.parseMediaType("image/png"), response.getHeaders().getContentType());

        String cacheControl = response.getHeaders().getCacheControl();
        Assertions.assertNotNull(cacheControl);
        Assertions.assertTrue(cacheControl.contains("no-cache"));

        Mockito.verify(service, Mockito.times(1)).getMyPhoto(authentication);
    }

    @Test
    public void getMyPhotoShouldThrowInvalidMediaTypeExceptionWhenContentTypeIsInvalid() {

        Authentication authentication = Mockito.mock(Authentication.class);

        byte[] photoBytes = "image-bytes".getBytes();
        UserPhotoDTO dto = new UserPhotoDTO(photoBytes, "image"); // inválido (sem /subtype)

        Mockito.when(service.getMyPhoto(authentication)).thenReturn(dto);

        Assertions.assertThrows(
                org.springframework.http.InvalidMediaTypeException.class,
                () -> controller.getMyPhoto(authentication)
        );

        Mockito.verify(service, Mockito.times(1)).getMyPhoto(authentication);
    }

    @Test
    public void forgotPasswordShouldReturnNoContentAndCallService() {

        ForgotPasswordDTO dto = new ForgotPasswordDTO();
        dto.setEmail("renan.duarte@email.com");

        Mockito.doNothing().when(service).requestPasswordReset(dto);

        ResponseEntity<Void> response = controller.forgotPassword(dto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(204, response.getStatusCodeValue());
        Assertions.assertNull(response.getBody());

        Mockito.verify(service, Mockito.times(1)).requestPasswordReset(dto);
    }

    @Test
    public void forgotPasswordShouldThrowRuntimeExceptionWhenServiceThrowsRuntimeException() {

        ForgotPasswordDTO dto = new ForgotPasswordDTO();
        dto.setEmail("renan.duarte@email.com");

        Mockito.doThrow(new RuntimeException("Erro SMTP"))
                .when(service).requestPasswordReset(dto);

        RuntimeException ex = Assertions.assertThrows(
                RuntimeException.class,
                () -> controller.forgotPassword(dto)
        );

        Assertions.assertEquals("Erro SMTP", ex.getMessage());
        Mockito.verify(service, Mockito.times(1)).requestPasswordReset(dto);
    }

    @Test
    public void forgotPasswordShouldThrowIllegalArgumentExceptionWhenServiceThrowsIllegalArgumentException() {

        ForgotPasswordDTO dto = new ForgotPasswordDTO();
        dto.setEmail("email_invalido");

        Mockito.doThrow(new IllegalArgumentException("Email inválido"))
                .when(service).requestPasswordReset(dto);

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> controller.forgotPassword(dto)
        );

        Assertions.assertEquals("Email inválido", ex.getMessage());
        Mockito.verify(service, Mockito.times(1)).requestPasswordReset(dto);
    }

    @Test
    public void resetPasswordShouldReturnNoContentAndCallService() {

        String token = "valid-token";
        NewPasswordDTO dto = new NewPasswordDTO("newPass123");

        Mockito.doNothing().when(service).resetPassword(token, dto);

        ResponseEntity<Void> response = controller.resetPassword(token, dto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(204, response.getStatusCodeValue());
        Assertions.assertNull(response.getBody());

        Mockito.verify(service, Mockito.times(1)).resetPassword(token, dto);
    }

    @Test
    public void resetPasswordShouldThrowRuntimeExceptionWhenServiceThrowsRuntimeException() {

        String token = "valid-token";
        NewPasswordDTO dto = new NewPasswordDTO("newPass123");

        Mockito.doThrow(new RuntimeException("Token inválido ou expirado"))
                .when(service).resetPassword(token, dto);

        RuntimeException ex = Assertions.assertThrows(
                RuntimeException.class,
                () -> controller.resetPassword(token, dto)
        );

        Assertions.assertEquals("Token inválido ou expirado", ex.getMessage());

        Mockito.verify(service, Mockito.times(1)).resetPassword(token, dto);
    }

    @Test
    public void resetPasswordShouldThrowIllegalArgumentExceptionWhenServiceThrowsIllegalArgumentException() {

        String token = "valid-token";
        NewPasswordDTO dto = new NewPasswordDTO("samePass123");

        Mockito.doThrow(new IllegalArgumentException("A nova senha não pode ser igual à senha atual"))
                .when(service).resetPassword(token, dto);

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> controller.resetPassword(token, dto)
        );

        Assertions.assertEquals("A nova senha não pode ser igual à senha atual", ex.getMessage());

        Mockito.verify(service, Mockito.times(1)).resetPassword(token, dto);
    }

    @Test
    public void getUserPhotoByIdShouldReturnOkWithBytesAndHeadersWhenServiceReturnsValidDTO() {

        Long existingId = 1L;

        byte[] photoBytes = "image-bytes".getBytes();
        UserPhotoDTO dto = new UserPhotoDTO(photoBytes, "image/png");

        Mockito.when(service.getUserPhotoById(existingId)).thenReturn(dto);

        ResponseEntity<byte[]> response = controller.getUserPhotoById(existingId);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());

        Assertions.assertNotNull(response.getBody());
        Assertions.assertArrayEquals(photoBytes, response.getBody());

        Assertions.assertEquals(MediaType.parseMediaType("image/png"), response.getHeaders().getContentType());

        String cacheControl = response.getHeaders().getCacheControl();
        Assertions.assertNotNull(cacheControl);
        Assertions.assertTrue(cacheControl.contains("no-cache"));

        Mockito.verify(service, Mockito.times(1)).getUserPhotoById(existingId);
    }

    @Test
    public void getUserPhotoByIdShouldReturnNoContentWhenServiceReturnsNull() {

        Long existingId = 1L;

        Mockito.when(service.getUserPhotoById(existingId)).thenReturn(null);

        ResponseEntity<byte[]> response = controller.getUserPhotoById(existingId);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(204, response.getStatusCodeValue());
        Assertions.assertNull(response.getBody());

        Mockito.verify(service, Mockito.times(1)).getUserPhotoById(existingId);
    }

    @Test
    public void getUserPhotoByIdShouldReturnNoContentWhenServiceReturnsEmptyPhoto() {

        Long existingId = 1L;

        UserPhotoDTO dto = new UserPhotoDTO(new byte[0], "image/jpeg"); // foto vazia -> 204

        Mockito.when(service.getUserPhotoById(existingId)).thenReturn(dto);

        ResponseEntity<byte[]> response = controller.getUserPhotoById(existingId);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(204, response.getStatusCodeValue());
        Assertions.assertNull(response.getBody());

        Mockito.verify(service, Mockito.times(1)).getUserPhotoById(existingId);
    }

}
