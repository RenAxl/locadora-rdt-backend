package com.locadora_rdt_backend.modules.financial.payables.constants;

public final class PayableErrorMessages {

    public static final String PAYABLE_NOT_FOUND = "Conta a pagar não encontrada.";
    public static final String DATABASE_INTEGRITY_VIOLATION = "Não foi possível excluir a conta a pagar.";
    public static final String PAYABLE_ALREADY_INSTALLMENTED = "Conta a pagar já foi parcelada.";
    public static final String PAID_PAYABLE_CANNOT_BE_UPDATED = "Conta a pagar paga não pode ser atualizada.";
    public static final String CANCELED_PAYABLE_CANNOT_BE_UPDATED = "Conta a pagar cancelada não pode ser atualizada.";
    public static final String CANCELED_PAYABLE_CANNOT_BE_PAID = "Conta a pagar cancelada não pode receber baixa.";

    private PayableErrorMessages() {
    }
}
