package com.locadora_rdt_backend.modules.identity.token.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class IdentityTokenService {

    private static final SecureRandom SECURE_RANDOM =
            new SecureRandom();

    private static final int TOKEN_BYTE_LENGTH = 32;

    public String generateToken() {

        byte[] bytes = new byte[TOKEN_BYTE_LENGTH];

        SECURE_RANDOM.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }
}