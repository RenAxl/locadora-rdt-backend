package com.locadora_rdt_backend.modules.organization.positions.constants;

public final class PositionAuthorizationExpressions {

    private PositionAuthorizationExpressions() {
    }

    public static final String POSITIONS_READ =
            "hasAuthority('POSITIONS_READ')";

    public static final String POSITIONS_WRITE =
            "hasAuthority('POSITIONS_WRITE')";

    public static final String POSITIONS_DELETE =
            "hasAuthority('POSITIONS_DELETE')";
}

