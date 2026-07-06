package com.locadora_rdt_backend.modules.financial.payables.constants;

public final class PayableErrorMessages {

    public static final String PAYABLE_NOT_FOUND = "Conta a pagar não encontrada.";
    public static final String DATABASE_INTEGRITY_VIOLATION = "Não foi possível excluir a conta a pagar.";
    public static final String PAYABLE_ALREADY_INSTALLMENTED = "Conta a pagar já foi parcelada.";

    private PayableErrorMessages() {
    }
}
