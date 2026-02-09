package com.locadora_rdt_backend.tests.security.jwt;

import com.locadora_rdt_backend.security.jwt.config.JwtTokenStoreConfig;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mockito;

import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class JwtTokenStoreConfigTests {

    @Test
    public void tokenStoreShouldReturnJwtTokenStore() {

        JwtTokenStoreConfig config = new JwtTokenStoreConfig();

        JwtAccessTokenConverter converter = Mockito.mock(JwtAccessTokenConverter.class);

        JwtTokenStore store = config.tokenStore(converter);

        Assertions.assertNotNull(store);
    }
}
