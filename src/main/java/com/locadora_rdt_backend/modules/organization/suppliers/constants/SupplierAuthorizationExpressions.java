package com.locadora_rdt_backend.modules.organization.suppliers.constants;

public final class SupplierAuthorizationExpressions {

    private SupplierAuthorizationExpressions() {
    }

    public static final String SUPPLIERS_READ =
            "hasAuthority('SUPPLIERS_READ')";

    public static final String SUPPLIERS_WRITE =
            "hasAuthority('SUPPLIERS_WRITE')";

    public static final String SUPPLIERS_DELETE =
            "hasAuthority('SUPPLIERS_DELETE')";
}
