package com.locadora_rdt_backend.modules.rentals.catalog.constants;

public final class CatalogAuthorizationExpressions {

    private CatalogAuthorizationExpressions() {
    }

    public static final String CATALOG_READ =
            "hasAuthority('CATALOG_READ')";

    public static final String CATALOG_WRITE =
            "hasAuthority('CATALOG_WRITE')";

    public static final String CATALOG_DELETE =
            "hasAuthority('CATALOG_DELETE')";
}

