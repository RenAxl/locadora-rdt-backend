package com.locadora_rdt_backend.modules.organization.customers.constants;

public final class CustomerAuthorizationExpressions {

    private CustomerAuthorizationExpressions() {
    }

    public static final String CUSTOMERS_READ =
            "hasAuthority('CUSTOMERS_READ')";

    public static final String CUSTOMERS_WRITE =
            "hasAuthority('CUSTOMERS_WRITE')";

    public static final String CUSTOMERS_DELETE =
            "hasAuthority('CUSTOMERS_DELETE')";
}
