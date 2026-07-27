package com.locadora_rdt_backend.modules.identity.passwordreset.service;

import com.locadora_rdt_backend.infrastructure.mail.service.EmailService;
import com.locadora_rdt_backend.infrastructure.mail.template.PasswordResetEmailTemplateService;
import com.locadora_rdt_backend.modules.identity.passwordreset.dto.ForgotPasswordDTO;
import com.locadora_rdt_backend.modules.identity.passwordreset.dto.NewPasswordDTO;
import com.locadora_rdt_backend.modules.identity.passwordreset.model.PasswordResetToken;
import com.locadora_rdt_backend.modules.identity.passwordreset.model.enums.TokenType;
import com.locadora_rdt_backend.modules.identity.passwordreset.repository.PasswordResetTokenRepository;
import com.locadora_rdt_backend.modules.identity.token.service.IdentityTokenService;
import com.locadora_rdt_backend.modules.identity.users.model.User;
import com.locadora_rdt_backend.modules.identity.users.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PasswordResetEmailTemplateService templateService;
    private final IdentityTokenService identityTokenService;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    @Value("${app.password-reset.token-minutes:30}")
    private long tokenMinutes;

    public PasswordResetService(
            UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService,
            PasswordResetEmailTemplateService templateService,
            IdentityTokenService identityTokenService
    ) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.templateService = templateService;
        this.identityTokenService = identityTokenService;
    }

    @Transactional
    public void requestPasswordReset(ForgotPasswordDTO dto) {

        if (dto == null
                || dto.getEmail() == null
                || dto.getEmail().isBlank()) {
            return;
        }

        User user = userRepository
                .findByEmail(dto.getEmail().trim());

        if (user == null) {
            return;
        }

        if (!user.isActive()) {
            return;
        }

        tokenRepository.deleteByUserIdAndType(
                user.getId(),
                TokenType.PASSWORD_RESET
        );

        String token = identityTokenService.generateToken();

        Instant expiration =
                Instant.now().plus(
                        tokenMinutes,
                        ChronoUnit.MINUTES
                );

        PasswordResetToken entity =
                new PasswordResetToken();

        entity.setToken(token);
        entity.setUser(user);
        entity.setExpiration(expiration);
        entity.setType(TokenType.PASSWORD_RESET);

        tokenRepository.save(entity);

        String link =
                UriComponentsBuilder
                        .fromHttpUrl(frontendBaseUrl)
                        .path("/auth/reset")
                        .queryParam("token", token)
                        .toUriString();

        String html =
                templateService.buildTemplate(
                        user.getName(),
                        link,
                        tokenMinutes
                );

        emailService.sendHtmlEmail(
                user.getEmail(),
                "Recuperação de senha - Locadora RDT",
                html
        );
    }

    @Transactional
    public void resetPassword(String token, NewPasswordDTO dto) {

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException(
                    "Token inválido"
            );
        }

        if (dto == null
                || dto.getPassword() == null
                || dto.getPassword().isBlank()) {

            throw new IllegalArgumentException(
                    "Senha inválida"
            );
        }

        PasswordResetToken entity =
                tokenRepository
                        .findByTokenAndTypeAndExpirationAfter(
                                token,
                                TokenType.PASSWORD_RESET,
                                Instant.now()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Token inválido ou expirado"
                                )
                        );

        User user = entity.getUser();

        if (user.getPassword() != null
                && passwordEncoder.matches(
                dto.getPassword(),
                user.getPassword()
        )) {

            throw new IllegalArgumentException(
                    "A nova senha não pode ser igual à senha atual"
            );
        }

        user.setPassword(
                passwordEncoder.encode(dto.getPassword())
        );

        userRepository.save(user);

        tokenRepository.delete(entity);
    }
}
