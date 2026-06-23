package com.locadora_rdt_backend.tests.modules.identity.controller;

import com.locadora_rdt_backend.modules.identity.activation.controller.AccountActivationController;
import com.locadora_rdt_backend.modules.identity.activation.service.AccountActivationService;
import com.locadora_rdt_backend.modules.identity.auth.security.CustomUserDetailsService;
import com.locadora_rdt_backend.modules.identity.auth.security.JwtClaimsTokenEnhancer;
import com.locadora_rdt_backend.modules.identity.passwordreset.controller.PasswordResetController;
import com.locadora_rdt_backend.modules.identity.passwordreset.dto.ForgotPasswordDTO;
import com.locadora_rdt_backend.modules.identity.passwordreset.dto.NewPasswordDTO;
import com.locadora_rdt_backend.modules.identity.passwordreset.service.PasswordResetService;
import com.locadora_rdt_backend.modules.users.model.User;
import com.locadora_rdt_backend.modules.users.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

@ExtendWith(MockitoExtension.class)
class IdentityControllerAndSecurityTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetService passwordResetService;

    @Mock
    private AccountActivationService activationService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Renan");
        user.setEmail("renan@email.com");
        user.setPassword("encoded");
        user.setActive(true);
    }

    @Test
    void jwtClaimsTokenEnhancerShouldReturnOriginalTokenWhenUserDoesNotExist() {
        JwtClaimsTokenEnhancer enhancer = new JwtClaimsTokenEnhancer(userRepository);
        OAuth2Authentication authentication = Mockito.mock(OAuth2Authentication.class);
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("token");
        Mockito.when(authentication.getName()).thenReturn("missing@email.com");
        Mockito.when(userRepository.findByEmail("missing@email.com")).thenReturn(null);

        OAuth2AccessToken result = enhancer.enhance(token, authentication);

        Assertions.assertSame(token, result);
        Assertions.assertTrue(result.getAdditionalInformation().isEmpty());
    }

    @Test
    void jwtClaimsTokenEnhancerShouldAddUserClaims() {
        JwtClaimsTokenEnhancer enhancer = new JwtClaimsTokenEnhancer(userRepository);
        OAuth2Authentication authentication = Mockito.mock(OAuth2Authentication.class);
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("token");
        Mockito.when(authentication.getName()).thenReturn("renan@email.com");
        Mockito.when(userRepository.findByEmail("renan@email.com")).thenReturn(user);

        OAuth2AccessToken result = enhancer.enhance(token, authentication);

        Assertions.assertEquals("Renan", result.getAdditionalInformation().get("userName"));
        Assertions.assertEquals(1L, result.getAdditionalInformation().get("userId"));
    }

    @Test
    void customUserDetailsServiceShouldLoadUserOrThrowWhenMissing() {
        CustomUserDetailsService service = new CustomUserDetailsService(userRepository);
        Mockito.when(userRepository.findByEmail("renan@email.com")).thenReturn(user);
        Mockito.when(userRepository.findByEmail("missing@email.com")).thenReturn(null);

        Assertions.assertSame(user, service.loadUserByUsername("renan@email.com"));
        Assertions.assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("missing@email.com"));
    }

    @Test
    void passwordResetControllerShouldDelegateAndReturnNoContent() {
        PasswordResetController controller = new PasswordResetController(passwordResetService);
        ForgotPasswordDTO forgotPasswordDTO = new ForgotPasswordDTO();
        forgotPasswordDTO.setEmail("renan@email.com");
        NewPasswordDTO newPasswordDTO = new NewPasswordDTO("new-password");

        ResponseEntity<Void> forgotResponse = controller.forgotPassword(forgotPasswordDTO);
        ResponseEntity<Void> resetResponse = controller.resetPassword("reset-token", newPasswordDTO);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, forgotResponse.getStatusCode());
        Assertions.assertEquals(HttpStatus.NO_CONTENT, resetResponse.getStatusCode());
        Mockito.verify(passwordResetService).requestPasswordReset(forgotPasswordDTO);
        Mockito.verify(passwordResetService).resetPassword("reset-token", newPasswordDTO);
    }

    @Test
    void accountActivationControllerShouldDelegateAndReturnNoContent() {
        AccountActivationController controller = new AccountActivationController(activationService);
        NewPasswordDTO dto = new NewPasswordDTO("new-password");

        ResponseEntity<Void> response = controller.activateAccount("activation-token", dto);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        Mockito.verify(activationService).activateAccount("activation-token", dto);
    }
}
