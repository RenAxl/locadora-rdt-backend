package com.locadora_rdt_backend.modules.financial.receivables.constants;

public final class ReceivableErrorMessages {

    public static final String RECEIVABLE_NOT_FOUND = "Conta a receber não encontrada.";
    public static final String DATABASE_INTEGRITY_VIOLATION = "Não foi possível excluir a conta a receber.";
    public static final String RECEIVABLE_ALREADY_INSTALLMENTED = "Conta a receber já foi parcelada.";

    private ReceivableErrorMessages() {
    }
}
