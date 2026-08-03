package com.locadora_rdt_backend.modules.stocks.items.constants;

public final class ItemAuthorizationExpressions {

    private ItemAuthorizationExpressions() {
    }

    public static final String ITEMS_READ =
            "hasAuthority('ITEMS_READ')";

    public static final String CLIENTS_ITEMS_READ =
            "hasAuthority('CLIENTS_ITEMS_READ')";

    public static final String ITEMS_WRITE =
            "hasAuthority('ITEMS_WRITE')";

    public static final String ITEMS_DELETE =
            "hasAuthority('ITEMS_DELETE')";
}
