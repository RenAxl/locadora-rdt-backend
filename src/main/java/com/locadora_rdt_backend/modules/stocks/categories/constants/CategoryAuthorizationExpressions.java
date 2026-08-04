package com.locadora_rdt_backend.modules.stocks.categories.constants;

public final class CategoryAuthorizationExpressions {

    private CategoryAuthorizationExpressions() {
    }

    public static final String CATEGORIES_READ =
            "hasAuthority('CATEGORIES_READ')";

    public static final String CUSTOMERS_CATEGORIES_READ =
            "hasAuthority('CUSTOMERS_CATEGORIES_READ')";

    public static final String CATEGORIES_WRITE =
            "hasAuthority('CATEGORIES_WRITE')";

    public static final String CATEGORIES_DELETE =
            "hasAuthority('CATEGORIES_DELETE')";
}
