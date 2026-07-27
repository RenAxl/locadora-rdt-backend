package com.locadora_rdt_backend.modules.settings.financialsettings.constants;

public final class FinancialSettingsAuthorizationExpressions {

    private FinancialSettingsAuthorizationExpressions() {
    }

    public static final String FINANCIALSETTINGS_READ =
            "hasAuthority('FINANCIALSETTINGS_READ')";

    public static final String FINANCIALSETTINGS_WRITE =
            "hasAuthority('FINANCIALSETTINGS_WRITE')";

}