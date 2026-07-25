package com.locadora_rdt_backend.modules.rentals.rentaltypes.constants;

public final class RentalTypeAuthorizationExpressions {

    private RentalTypeAuthorizationExpressions() {
    }

    public static final String RENTAL_TYPES_READ =
            "hasAuthority('RENTAL_TYPES_READ')";

    public static final String RENTAL_TYPES_WRITE =
            "hasAuthority('RENTAL_TYPES_WRITE')";

    public static final String RENTAL_TYPES_DELETE =
            "hasAuthority('RENTAL_TYPES_DELETE')";
}
