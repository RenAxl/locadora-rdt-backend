package com.locadora_rdt_backend.modules.stocks.stockbalances.constants;

public class StockBalanceAuthorizationExpressions {

    private StockBalanceAuthorizationExpressions() {
    }

    public static final String STOCKBALANCES_READ =
            "hasAuthority('STOCKBALANCES_READ')";

    public static final String STOCKBALANCES_WRITE =
            "hasAuthority('STOCKBALANCES_WRITE')";

    public static final String STOCKBALANCES_DELETE =
            "hasAuthority('STOCKBALANCES_DELETE')";
}
