package com.locadora_rdt_backend.tests.config;

import com.locadora_rdt_backend.config.security.CorsConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@ExtendWith(SpringExtension.class)
public class CorsConfigTests {

    @Test
    public void corsConfigurationSourceShouldReturnConfiguredCors() {

        CorsConfig config = new CorsConfig();
        CorsConfigurationSource source = config.corsConfigurationSource();

        Assertions.assertNotNull(source);

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setRequestURI("/users");

        CorsConfiguration cors = source.getCorsConfiguration(req);

        Assertions.assertNotNull(cors);

        Assertions.assertTrue(cors.getAllowedOriginPatterns().contains("*"));
        Assertions.assertTrue(cors.getAllowedMethods().contains("GET"));
        Assertions.assertTrue(cors.getAllowedMethods().contains("POST"));
        Assertions.assertTrue(cors.getAllowedMethods().contains("PUT"));
        Assertions.assertTrue(cors.getAllowedMethods().contains("DELETE"));
        Assertions.assertTrue(cors.getAllowedMethods().contains("PATCH"));
        Assertions.assertTrue(cors.getAllowedMethods().contains("OPTIONS"));

        Assertions.assertTrue(cors.getAllowCredentials());

        Assertions.assertTrue(cors.getAllowedHeaders().contains("Authorization"));
        Assertions.assertTrue(cors.getAllowedHeaders().contains("Content-Type"));

        Assertions.assertTrue(cors.getExposedHeaders().contains("Authorization"));
    }
}
