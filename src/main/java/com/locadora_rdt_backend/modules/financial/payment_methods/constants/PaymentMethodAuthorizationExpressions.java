package com.locadora_rdt_backend.modules.financial.payment_methods.constants;

public final class PaymentMethodAuthorizationExpressions {

    private PaymentMethodAuthorizationExpressions() {
    }

    public static final String METHODS_READ =
            "hasAuthority('METHODS_READ')";

    public static final String METHODS_WRITE =
            "hasAuthority('METHODS_WRITE')";

    public static final String METHODS_DELETE =
            "hasAuthority('METHODS_DELETE')";
}
