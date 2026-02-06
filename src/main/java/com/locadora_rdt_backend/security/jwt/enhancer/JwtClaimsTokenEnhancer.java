package com.locadora_rdt_backend.security.jwt.enhancer;

import java.util.HashMap;
import java.util.Map;

import com.locadora_rdt_backend.entities.User;
import com.locadora_rdt_backend.repositories.UserRepository;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;


@Component
public class JwtClaimsTokenEnhancer implements TokenEnhancer {

    private final UserRepository userRepository;

    public JwtClaimsTokenEnhancer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return accessToken;
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("userName", user.getName());
        claims.put("userId", user.getId());

        DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
        token.setAdditionalInformation(claims);

        return token;
    }
}

