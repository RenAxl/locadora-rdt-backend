package com.locadora_rdt_backend.modules.financial.payables.constants;

public final class PayableValidationConstants {

    public static final int DESCRIPTION_MIN_LENGTH = 3;
    public static final int DESCRIPTION_MAX_LENGTH = 120;
    public static final long MINIMUM_INSTALLMENTS = 2L;
    public static final String MINIMUM_AMOUNT = "0.01";
    public static final String DESCRIPTION_REQUIRED = "Descrição é obrigatória";
    public static final String DESCRIPTION_LENGTH = "Descrição deve ter entre 3 e 120 caracteres";
    public static final String AMOUNT_REQUIRED = "Valor é obrigatório";
    public static final String AMOUNT_MUST_BE_POSITIVE = "O valor deve ser maior que zero";
    public static final String DUE_DATE_REQUIRED = "Vencimento é obrigatório";
    public static final String PAYMENT_AMOUNT_REQUIRED = "Informe o valor da baixa";
    public static final String PAYMENT_AMOUNT_MUST_BE_POSITIVE = "Valor de baixa deve ser maior que zero";
    public static final String INSTALLMENTS_REQUIRED = "Informe a quantidade de parcelas";
    public static final String MINIMUM_INSTALLMENTS_REQUIRED =
            "O parcelamento deve ter pelo menos duas parcelas";

    private PayableValidationConstants() {
    }
}
