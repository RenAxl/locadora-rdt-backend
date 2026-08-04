package com.locadora_rdt_backend.modules.rentals.rental.constants;

public final class RentalAuthorizationExpressions {

    private RentalAuthorizationExpressions() {
    }

    public static final String RENTALS_READ =
            "hasAuthority('RENTALS_READ')";

    public static final String RENTALS_WRITE =
            "hasAuthority('RENTALS_WRITE')";

    public static final String RENTALS_DELETE =
            "hasAuthority('RENTALS_DELETE')";

    public static final String CUSTOMER_RENTALS =
            "hasAuthority('CUSTOMER_RENTALS')";
}

