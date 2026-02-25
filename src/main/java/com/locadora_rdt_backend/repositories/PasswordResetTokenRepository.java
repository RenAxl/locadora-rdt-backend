package com.locadora_rdt_backend.repositories;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.locadora_rdt_backend.entities.PasswordResetToken;
import com.locadora_rdt_backend.entities.enums.TokenType;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenAndTypeAndExpirationAfter(String token, TokenType type, Instant now);

    void deleteByUserIdAndType(Long userId, TokenType type);
}