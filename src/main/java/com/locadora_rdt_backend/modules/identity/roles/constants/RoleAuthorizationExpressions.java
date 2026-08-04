package com.locadora_rdt_backend.modules.identity.roles.constants;

public class RoleAuthorizationExpressions {

    private RoleAuthorizationExpressions() {
    }

    public static final String ROLE_READ =
            "hasAuthority('ROLE_READ')";

    public static final String ROLE_WRITE =
            "hasAuthority('ROLE_WRITE')";

    public static final String ROLE_DELETE =
            "hasAuthority('ROLE_DELETE')";
}
