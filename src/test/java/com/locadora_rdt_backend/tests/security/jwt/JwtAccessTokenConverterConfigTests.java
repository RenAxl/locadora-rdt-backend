package com.locadora_rdt_backend.tests.security.jwt;

import com.locadora_rdt_backend.security.jwt.config.JwtAccessTokenConverterConfig;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(SpringExtension.class)
public class JwtAccessTokenConverterConfigTests {

    @Test
    public void accessTokenConverterShouldUseSigningKeyFromValue() {

        JwtAccessTokenConverterConfig config = new JwtAccessTokenConverterConfig();
        ReflectionTestUtils.setField(config, "jwtSecret", "my-secret");

        JwtAccessTokenConverter converter = config.accessTokenConverter();

        Assertions.assertNotNull(converter);

        Assertions.assertNotNull(converter.getKey());
    }
}
