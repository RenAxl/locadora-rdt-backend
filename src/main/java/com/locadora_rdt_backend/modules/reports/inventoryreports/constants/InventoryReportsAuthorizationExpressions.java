package com.locadora_rdt_backend.modules.reports.inventoryreports.constants;

public final class InventoryReportsAuthorizationExpressions {

    private InventoryReportsAuthorizationExpressions() {
    }

    public static final String INVENTORYREPORTS_READ =
            "hasAuthority('INVENTORYREPORTS_READ')";

}
