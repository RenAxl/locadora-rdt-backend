package com.locadora_rdt_backend.modules.identity.activation.service;

import com.locadora_rdt_backend.infrastructure.mail.service.EmailService;
import com.locadora_rdt_backend.infrastructure.mail.template.ActivationEmailTemplateService;
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
public class AccountActivationService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final ActivationEmailTemplateService templateService;
    private final IdentityTokenService identityTokenService;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    @Value("${app.activation.token-minutes:30}")
    private long tokenMinutes;

    public AccountActivationService(
            PasswordResetTokenRepository tokenRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService,
            ActivationEmailTemplateService templateService,
            IdentityTokenService identityTokenService
    ) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.templateService = templateService;
        this.identityTokenService = identityTokenService;
    }

    @Transactional
    public void createActivationTokenAndSendEmail(User user) {

        String token = identityTokenService.generateToken();

        Instant expiration = Instant.now()
                .plus(tokenMinutes, ChronoUnit.MINUTES);

        tokenRepository.deleteByUserIdAndType(
                user.getId(),
                TokenType.ACTIVATION
        );

        PasswordResetToken entity = new PasswordResetToken();
        entity.setToken(token);
        entity.setUser(user);
        entity.setExpiration(expiration);
        entity.setType(TokenType.ACTIVATION);

        tokenRepository.save(entity);

        String link = UriComponentsBuilder
                .fromHttpUrl(frontendBaseUrl)
                .path("/auth/activate")
                .queryParam("token", token)
                .toUriString();

        String html = templateService.buildTemplate(
                user.getName(),
                link,
                tokenMinutes
        );

        emailService.sendHtmlEmail(
                user.getEmail(),
                "Ative sua conta - Locadora RDT",
                html
        );
    }

    @Transactional
    public void activateAccount(String token, NewPasswordDTO dto) {

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token inválido");
        }

        if (dto == null || dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException("Senha inválida");
        }

        PasswordResetToken entity = tokenRepository
                .findByTokenAndTypeAndExpirationAfter(
                        token,
                        TokenType.ACTIVATION,
                        Instant.now()
                )
                .orElseThrow(() ->
                        new IllegalArgumentException("Token inválido ou expirado")
                );

        User user = entity.getUser();

        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setActive(true);

        userRepository.save(user);

        tokenRepository.delete(entity);
    }
}
