package com.locadora_rdt_backend.modules.reports.financialreports.constants;

public final class FinancialReportsAuthorizationExpressions {

    private FinancialReportsAuthorizationExpressions() {
    }

    public static final String FINANCIALREPORTS_READ =
            "hasAuthority('FINANCIALREPORTS_READ')";

}
