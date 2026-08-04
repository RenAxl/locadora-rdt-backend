package com.locadora_rdt_backend.modules.identity.users.constants;

public class UserAuthorizationExpressions {

    private UserAuthorizationExpressions() {
    }

    public static final String USER_READ =
            "hasAuthority('USER_READ')";

    public static final String USER_WRITE =
            "hasAuthority('USER_WRITE')";

    public static final String USER_DELETE =
            "hasAuthority('USER_DELETE')";

    public static final String USER_PROFILE_READ =
            "hasAuthority('USER_PROFILE_READ')";

    public static final String USER_PROFILE_WRITE =
            "hasAuthority('USER_PROFILE_WRITE')";
}

