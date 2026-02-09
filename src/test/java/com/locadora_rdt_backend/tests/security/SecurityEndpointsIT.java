package com.locadora_rdt_backend.tests.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "OAUTH_CLIENT_ID=test-client",
        "OAUTH_CLIENT_SECRET=test-secret",
        "JWT_SECRET=test-jwt-secret",
        "JWT_DURATION=3600",
        "spring.mail.host=localhost",
        "spring.mail.port=2525",
        "spring.mail.username=test",
        "spring.mail.password=test"
})
public class SecurityEndpointsIT {

    @Autowired
    private MockMvc mockMvc;

    private static final String[] PUBLIC_GET = {
            "/",
            "/actuator/health",
            "/h2-console",
            "/users"
    };

    private static final String[] PROTECTED_GET = {

    };

    private static final String[] ADMIN_ONLY = {

    };

    private static final String[] STATE_CHANGING = {
            "/users"
    };

    static Stream<String> publicGetProvider() { return Stream.of(PUBLIC_GET); }
    static Stream<String> protectedGetProvider() { return Stream.of(PROTECTED_GET); }
    static Stream<String> adminOnlyProvider() { return Stream.of(ADMIN_ONLY); }
    static Stream<String> stateChangingProvider() { return Stream.of(STATE_CHANGING); }

    private static boolean isNotBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    @Nested
    class PublicVsProtected {

        @ParameterizedTest(name = "GET público {0} não deve retornar 401/403")
        @MethodSource("com.locadora_rdt_backend.tests.security.SecurityEndpointsIT#publicGetProvider")
        void publicGet_shouldNotBeUnauthorizedOrForbidden(String url) throws Exception {
            if (!isNotBlank(url)) return;

            mockMvc.perform(get(url))
                    .andExpect(result -> {
                        int sc = result.getResponse().getStatus();
                        if (sc == 401 || sc == 403) {
                            throw new AssertionError("Endpoint público não deveria retornar 401/403. Status=" + sc + ", url=" + url);
                        }
                    });
        }

        @Test
        @DisplayName("Bearer inválido só deve dar 401 se seu app realmente valida Bearer nos endpoints (resource server)")
        void invalidBearer_shouldNotBeAssumed() throws Exception {

            mockMvc.perform(get("/users").header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class Csrf {

        @ParameterizedTest(name = "POST em {0} com CSRF + auth não deve falhar na camada de segurança")
        @MethodSource("com.locadora_rdt_backend.tests.security.SecurityEndpointsIT#stateChangingProvider")
        void post_withCsrf_shouldNotFailSecurityLayer(String url) throws Exception {
            if (!isNotBlank(url)) return;

            mockMvc.perform(post(url)
                            .with(user("admin").roles("ADMIN"))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(result -> {
                        int sc = result.getResponse().getStatus();
                        if (sc == 401 || sc == 403) {
                            throw new AssertionError("Com CSRF + auth, não era pra falhar na segurança. Status=" + sc + ", url=" + url);
                        }
                    });
        }
    }

    @Nested
    class Cors {

        @Test
        @DisplayName("Preflight OPTIONS não deve retornar 401")
        void preflightOptions_shouldNotBeUnauthorized() throws Exception {
            String url = "/users";

            mockMvc.perform(options(url)
                            .header(HttpHeaders.ORIGIN, "http://localhost:4200")
                            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "Authorization,Content-Type"))
                    .andExpect(result -> {
                        int sc = result.getResponse().getStatus();
                        if (sc == 401) {
                            throw new AssertionError("Preflight não deveria retornar 401. Ajuste CORS. Status=" + sc);
                        }
                    });
        }
    }

    @Nested
    class SecurityHeaders {

        @Test
        @DisplayName("Deve retornar headers básicos quando configurado")
        void shouldHaveBasicSecurityHeadersWhenConfigured() throws Exception {
            mockMvc.perform(get("/users"))
                    .andExpect(header().exists("X-Content-Type-Options"))
                    .andExpect(header().exists("X-Frame-Options"));
        }
    }
}
