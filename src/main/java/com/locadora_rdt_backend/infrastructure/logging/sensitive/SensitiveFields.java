package com.locadora_rdt_backend.infrastructure.logging.sensitive;

import java.util.Set;

public final class SensitiveFields {

    private SensitiveFields() {
    }

    public static final Set<String> FIELDS = Set.of(
            "password",
            "newPassword",
            "token",
            "refreshToken",
            "authorization",
            "jwt",
            "secret",
            "clientSecret"
    );
}