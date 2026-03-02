package com.locadora_rdt_backend.modules.identity.passwordreset.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.locadora_rdt_backend.modules.identity.passwordreset.model.PasswordResetToken;
import com.locadora_rdt_backend.modules.identity.passwordreset.model.enums.TokenType;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenAndTypeAndExpirationAfter(String token, TokenType type, Instant now);

    void deleteByUserIdAndType(Long userId, TokenType type);
}