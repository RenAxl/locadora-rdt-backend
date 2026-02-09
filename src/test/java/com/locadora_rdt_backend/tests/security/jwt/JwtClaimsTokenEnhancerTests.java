package com.locadora_rdt_backend.tests.security.jwt;

import com.locadora_rdt_backend.entities.User;
import com.locadora_rdt_backend.repositories.UserRepository;
import com.locadora_rdt_backend.security.jwt.enhancer.JwtClaimsTokenEnhancer;
import com.locadora_rdt_backend.tests.factory.UserFactory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(SpringExtension.class)
public class JwtClaimsTokenEnhancerTests {

    @InjectMocks
    private JwtClaimsTokenEnhancer enhancer;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OAuth2Authentication authentication;

    private User user;

    @BeforeEach
    void setUp() throws Exception {
        user = UserFactory.createUser();

        Mockito.when(authentication.getName()).thenReturn("renan@email.com");

        Mockito.when(userRepository.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(user);
    }

    @Test
    public void enhanceShouldAddClaimsWhenUserExists() {

        DefaultOAuth2AccessToken accessToken = new DefaultOAuth2AccessToken("token");

        // Se você setar algo aqui, o seu enhancer sobrescreve (então NÃO devemos validar que preservou)
        Map<String, Object> before = new HashMap<>();
        before.put("foo", "bar");
        accessToken.setAdditionalInformation(before);

        var result = enhancer.enhance(accessToken, authentication);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result instanceof DefaultOAuth2AccessToken);

        DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) result;

        // ✅ Valida o comportamento real do seu código: SOBRESCREVE additionalInformation
        Assertions.assertFalse(token.getAdditionalInformation().containsKey("foo"));

        Assertions.assertEquals(user.getName(), token.getAdditionalInformation().get("userName"));
        Assertions.assertEquals(user.getId(), token.getAdditionalInformation().get("userId"));

        Mockito.verify(userRepository, Mockito.times(1)).findByEmail("renan@email.com");
    }

    @Test
    public void enhanceShouldNotChangeClaimsWhenUserDoesNotExist() {

        Mockito.when(userRepository.findByEmail("renan@email.com"))
                .thenReturn(null);

        DefaultOAuth2AccessToken accessToken = new DefaultOAuth2AccessToken("token");

        var result = enhancer.enhance(accessToken, authentication);

        DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) result;

        // quando user == null, seu enhancer retorna o accessToken sem setAdditionalInformation(claims)
        Assertions.assertTrue(token.getAdditionalInformation() == null || token.getAdditionalInformation().isEmpty());
    }
}
