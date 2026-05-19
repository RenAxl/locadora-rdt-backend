package com.locadora_rdt_backend.infrastructure.web.filter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SpringSecurityAuthenticatedUsernameResolver implements AuthenticatedUsernameResolver {

    @Override
    public String resolve() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return WebFilterConstants.ANONYMOUS;
        }

        String username = authentication.getName();

        if (username == null || username.trim().isEmpty()) {
            return WebFilterConstants.ANONYMOUS;
        }

        return username;
    }
}
