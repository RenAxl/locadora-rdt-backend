package com.locadora_rdt_backend.tests.modules.identity.service;

import com.locadora_rdt_backend.infrastructure.mail.service.EmailService;
import com.locadora_rdt_backend.infrastructure.mail.template.ActivationEmailTemplateService;
import com.locadora_rdt_backend.infrastructure.mail.template.PasswordResetEmailTemplateService;
import com.locadora_rdt_backend.modules.identity.activation.service.AccountActivationService;
import com.locadora_rdt_backend.modules.identity.passwordreset.dto.ForgotPasswordDTO;
import com.locadora_rdt_backend.modules.identity.passwordreset.dto.NewPasswordDTO;
import com.locadora_rdt_backend.modules.identity.passwordreset.model.PasswordResetToken;
import com.locadora_rdt_backend.modules.identity.passwordreset.model.enums.TokenType;
import com.locadora_rdt_backend.modules.identity.passwordreset.repository.PasswordResetTokenRepository;
import com.locadora_rdt_backend.modules.identity.passwordreset.service.PasswordResetService;
import com.locadora_rdt_backend.modules.identity.token.service.IdentityTokenService;
import com.locadora_rdt_backend.modules.users.model.User;
import com.locadora_rdt_backend.modules.users.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class IdentityServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordResetEmailTemplateService passwordTemplateService;

    @Mock
    private ActivationEmailTemplateService activationTemplateService;

    @Mock
    private IdentityTokenService identityTokenService;

    private PasswordResetService passwordResetService;
    private AccountActivationService activationService;
    private User user;

    @BeforeEach
    void setUp() {
        passwordResetService = new PasswordResetService(
                userRepository,
                tokenRepository,
                passwordEncoder,
                emailService,
                passwordTemplateService,
                identityTokenService
        );
        activationService = new AccountActivationService(
                tokenRepository,
                userRepository,
                passwordEncoder,
                emailService,
                activationTemplateService,
                identityTokenService
        );
        ReflectionTestUtils.setField(passwordResetService, "frontendBaseUrl", "http://frontend");
        ReflectionTestUtils.setField(passwordResetService, "tokenMinutes", 30L);
        ReflectionTestUtils.setField(activationService, "frontendBaseUrl", "http://frontend");
        ReflectionTestUtils.setField(activationService, "tokenMinutes", 15L);

        user = new User();
        user.setId(1L);
        user.setName("Renan");
        user.setEmail("renan@email.com");
        user.setActive(true);
        user.setPassword("encoded-old-password");
    }

    @Test
    void requestPasswordResetShouldIgnoreInvalidUnknownOrInactiveUser() {
        passwordResetService.requestPasswordReset(null);

        ForgotPasswordDTO blankEmail = new ForgotPasswordDTO();
        blankEmail.setEmail(" ");
        passwordResetService.requestPasswordReset(blankEmail);

        ForgotPasswordDTO unknownEmail = new ForgotPasswordDTO();
        unknownEmail.setEmail("unknown@email.com");
        Mockito.when(userRepository.findByEmail("unknown@email.com")).thenReturn(null);
        passwordResetService.requestPasswordReset(unknownEmail);

        ForgotPasswordDTO inactiveEmail = new ForgotPasswordDTO();
        inactiveEmail.setEmail("inactive@email.com");
        user.setActive(false);
        Mockito.when(userRepository.findByEmail("inactive@email.com")).thenReturn(user);
        passwordResetService.requestPasswordReset(inactiveEmail);

        Mockito.verify(tokenRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(emailService, Mockito.never()).sendHtmlEmail(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void requestPasswordResetShouldCreateTokenAndSendEmail() {
        ForgotPasswordDTO dto = new ForgotPasswordDTO();
        dto.setEmail(" renan@email.com ");
        Mockito.when(userRepository.findByEmail("renan@email.com")).thenReturn(user);
        Mockito.when(identityTokenService.generateToken()).thenReturn("reset-token");
        Mockito.when(passwordTemplateService.buildTemplate(
                Mockito.eq("Renan"),
                Mockito.contains("reset-token"),
                Mockito.eq(30L)
        )).thenReturn("<html>reset</html>");

        passwordResetService.requestPasswordReset(dto);

        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        Mockito.verify(tokenRepository).deleteByUserIdAndType(1L, TokenType.PASSWORD_RESET);
        Mockito.verify(tokenRepository).save(tokenCaptor.capture());
        Mockito.verify(emailService).sendHtmlEmail(
                "renan@email.com",
                "Recuperação de senha - Locadora RDT",
                "<html>reset</html>"
        );

        PasswordResetToken token = tokenCaptor.getValue();
        Assertions.assertEquals("reset-token", token.getToken());
        Assertions.assertEquals(TokenType.PASSWORD_RESET, token.getType());
        Assertions.assertSame(user, token.getUser());
        Assertions.assertNotNull(token.getExpiration());
    }

    @Test
    void resetPasswordShouldValidateInputAndToken() {
        NewPasswordDTO validPassword = new NewPasswordDTO("123456");
        NewPasswordDTO blankPassword = new NewPasswordDTO(" ");

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> passwordResetService.resetPassword(null, validPassword));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> passwordResetService.resetPassword("token", null));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> passwordResetService.resetPassword("token", blankPassword));

        Mockito.when(tokenRepository.findByTokenAndTypeAndExpirationAfter(
                Mockito.eq("missing"),
                Mockito.eq(TokenType.PASSWORD_RESET),
                Mockito.any(Instant.class)
        )).thenReturn(Optional.empty());

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> passwordResetService.resetPassword("missing", validPassword));
    }

    @Test
    void resetPasswordShouldRejectSamePassword() {
        PasswordResetToken token = createToken(TokenType.PASSWORD_RESET);
        Mockito.when(tokenRepository.findByTokenAndTypeAndExpirationAfter(
                Mockito.eq("reset-token"),
                Mockito.eq(TokenType.PASSWORD_RESET),
                Mockito.any(Instant.class)
        )).thenReturn(Optional.of(token));
        Mockito.when(passwordEncoder.matches("new-password", "encoded-old-password")).thenReturn(true);
        NewPasswordDTO newPassword = new NewPasswordDTO("new-password");

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> passwordResetService.resetPassword("reset-token", newPassword));
    }

    @Test
    void resetPasswordShouldEncodeNewPasswordAndDeleteToken() {
        PasswordResetToken token = createToken(TokenType.PASSWORD_RESET);
        Mockito.when(tokenRepository.findByTokenAndTypeAndExpirationAfter(
                Mockito.eq("reset-token"),
                Mockito.eq(TokenType.PASSWORD_RESET),
                Mockito.any(Instant.class)
        )).thenReturn(Optional.of(token));
        Mockito.when(passwordEncoder.matches("new-password", "encoded-old-password")).thenReturn(false);
        Mockito.when(passwordEncoder.encode("new-password")).thenReturn("encoded-new-password");

        passwordResetService.resetPassword("reset-token", new NewPasswordDTO("new-password"));

        Assertions.assertEquals("encoded-new-password", user.getPassword());
        Mockito.verify(userRepository).save(user);
        Mockito.verify(tokenRepository).delete(token);
    }

    @Test
    void createActivationTokenShouldSaveTokenAndSendEmail() {
        Mockito.when(identityTokenService.generateToken()).thenReturn("activation-token");
        Mockito.when(activationTemplateService.buildTemplate(
                Mockito.eq("Renan"),
                Mockito.contains("activation-token"),
                Mockito.eq(15L)
        )).thenReturn("<html>activation</html>");

        activationService.createActivationTokenAndSendEmail(user);

        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        Mockito.verify(tokenRepository).deleteByUserIdAndType(1L, TokenType.ACTIVATION);
        Mockito.verify(tokenRepository).save(tokenCaptor.capture());
        Mockito.verify(emailService).sendHtmlEmail(
                "renan@email.com",
                "Ative sua conta - Locadora RDT",
                "<html>activation</html>"
        );

        PasswordResetToken token = tokenCaptor.getValue();
        Assertions.assertEquals("activation-token", token.getToken());
        Assertions.assertEquals(TokenType.ACTIVATION, token.getType());
        Assertions.assertSame(user, token.getUser());
    }

    @Test
    void activateAccountShouldValidateInputAndToken() {
        NewPasswordDTO validPassword = new NewPasswordDTO("123456");
        NewPasswordDTO blankPassword = new NewPasswordDTO(" ");

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> activationService.activateAccount(null, validPassword));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> activationService.activateAccount("token", null));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> activationService.activateAccount("token", blankPassword));

        Mockito.when(tokenRepository.findByTokenAndTypeAndExpirationAfter(
                Mockito.eq("missing"),
                Mockito.eq(TokenType.ACTIVATION),
                Mockito.any(Instant.class)
        )).thenReturn(Optional.empty());

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> activationService.activateAccount("missing", validPassword));
    }

    @Test
    void activateAccountShouldEncodePasswordActivateUserAndDeleteToken() {
        user.setActive(false);
        PasswordResetToken token = createToken(TokenType.ACTIVATION);
        Mockito.when(tokenRepository.findByTokenAndTypeAndExpirationAfter(
                Mockito.eq("activation-token"),
                Mockito.eq(TokenType.ACTIVATION),
                Mockito.any(Instant.class)
        )).thenReturn(Optional.of(token));
        Mockito.when(passwordEncoder.encode("new-password")).thenReturn("encoded-new-password");

        activationService.activateAccount("activation-token", new NewPasswordDTO("new-password"));

        Assertions.assertEquals("encoded-new-password", user.getPassword());
        Assertions.assertTrue(user.isActive());
        Mockito.verify(userRepository).save(user);
        Mockito.verify(tokenRepository).delete(token);
    }

    @Test
    void identityTokenServiceShouldGenerateUrlSafeToken() {
        String token = new IdentityTokenService().generateToken();

        Assertions.assertNotNull(token);
        Assertions.assertFalse(token.isBlank());
        Assertions.assertFalse(token.contains("="));
    }

    private PasswordResetToken createToken(TokenType type) {
        PasswordResetToken token = new PasswordResetToken();
        token.setId(1L);
        token.setToken(type == TokenType.ACTIVATION ? "activation-token" : "reset-token");
        token.setType(type);
        token.setExpiration(Instant.parse("2026-01-01T10:01:00Z"));
        token.setUser(user);
        return token;
    }
}
