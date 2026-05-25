package com.locadora_rdt_backend.infrastructure.logging.sensitive;

public final class SensitiveDataPatterns {

    private SensitiveDataPatterns() {
    }

    public static final String CPF =
            "\\d{11}";

    public static final String EMAIL =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    public static final String JWT_TOKEN =
            "^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+$";

    public static final String BEARER_TOKEN =
            "^Bearer\\s.+$";

    public static final String PASSWORD =
            ".*password.*";

    public static final String SECRET =
            ".*secret.*";
}
