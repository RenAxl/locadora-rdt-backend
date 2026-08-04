package com.locadora_rdt_backend.modules.rentals.rentalhistory.constants;

public class RentalHistoryAuthorizationExpressions {

    private RentalHistoryAuthorizationExpressions() {
    }

    public static final String RENTALS_HISTORY_READ =
            "hasAuthority('RENTALS_HISTORY_READ')";

}
