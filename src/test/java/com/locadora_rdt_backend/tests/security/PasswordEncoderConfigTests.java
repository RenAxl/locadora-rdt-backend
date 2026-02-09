package com.locadora_rdt_backend.tests.security;

import com.locadora_rdt_backend.security.core.PasswordEncoderConfig;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class PasswordEncoderConfigTests {

    @Test
    public void passwordEncoderShouldReturnBCryptPasswordEncoder() {

        PasswordEncoderConfig config = new PasswordEncoderConfig();
        BCryptPasswordEncoder encoder = config.passwordEncoder();

        Assertions.assertNotNull(encoder);

        String hash = encoder.encode("123456");
        Assertions.assertNotNull(hash);
        Assertions.assertNotEquals("123456", hash);

        Assertions.assertTrue(encoder.matches("123456", hash));
    }
}
