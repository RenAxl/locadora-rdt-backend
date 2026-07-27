package com.locadora_rdt_backend.modules.financial.receivables.constants;

public final class ReceivableAuthorizationExpressions {

    private ReceivableAuthorizationExpressions() {
    }

    public static final String RECEIVABLES_READ =
            "hasAuthority('RECEIVABLES_READ')";

    public static final String RECEIVABLES_WRITE =
            "hasAuthority('RECEIVABLES_WRITE')";

    public static final String RECEIVABLES_DELETE =
            "hasAuthority('RECEIVABLES_DELETE')";
}
