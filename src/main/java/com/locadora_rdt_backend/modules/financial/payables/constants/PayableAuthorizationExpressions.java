package com.locadora_rdt_backend.modules.financial.payables.constants;

public final class PayableAuthorizationExpressions {

    private PayableAuthorizationExpressions() {
    }

    public static final String PAYABLES_READ =
            "hasAuthority('PAYABLES_READ')";

    public static final String PAYABLES_WRITE =
            "hasAuthority('PAYABLES_WRITE')";

    public static final String PAYABLES_DELETE =
            "hasAuthority('PAYABLES_DELETE')";

}
