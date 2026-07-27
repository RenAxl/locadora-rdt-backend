package com.locadora_rdt_backend.modules.financial.payment.frequencies.constants;

public final class PaymentFrequencyAuthorizationExpressions {

    private PaymentFrequencyAuthorizationExpressions() {
    }

    public static final String FREQUENCIES_READ =
            "hasAuthority('FREQUENCIES_READ')";

    public static final String FREQUENCIES_WRITE =
            "hasAuthority('FREQUENCIES_WRITE')";

    public static final String FREQUENCIES_DELETE =
            "hasAuthority('FREQUENCIES_DELETE')";
}

