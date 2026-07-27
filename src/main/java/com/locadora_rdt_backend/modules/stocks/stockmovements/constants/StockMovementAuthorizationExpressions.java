package com.locadora_rdt_backend.modules.stocks.stockmovements.constants;

public final class StockMovementAuthorizationExpressions {

    private StockMovementAuthorizationExpressions() {
    }

    public static final String STOCKMOVEMENTS_READ =
            "hasAuthority('STOCKMOVEMENTS_READ')";

    public static final String STOCKMOVEMENTS_WRITE =
            "hasAuthority('STOCKMOVEMENTS_WRITE')";

    public static final String STOCKMOVEMENTS_DELETE =
            "hasAuthority('STOCKMOVEMENTS_DELETE')";
}
