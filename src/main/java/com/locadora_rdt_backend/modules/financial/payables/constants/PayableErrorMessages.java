package com.locadora_rdt_backend.modules.financial.payables.constants;

public final class PayableErrorMessages {

    public static final String PAYABLE_NOT_FOUND = "Conta a pagar não encontrada.";
    public static final String DATABASE_INTEGRITY_VIOLATION = "Não foi possível excluir a conta a pagar.";
    public static final String PAYABLE_ALREADY_INSTALLMENTED = "Conta a pagar já foi parcelada.";
    public static final String PAID_PAYABLE_CANNOT_BE_UPDATED = "Conta a pagar paga não pode ser atualizada.";
    public static final String CANCELED_PAYABLE_CANNOT_BE_UPDATED = "Conta a pagar cancelada não pode ser atualizada.";
    public static final String CANCELED_PAYABLE_CANNOT_BE_PAID = "Conta a pagar cancelada não pode receber baixa.";
    public static final String PAID_PAYABLE_CANNOT_BE_PAID = "Conta já está paga.";
    public static final String PAYMENT_AMOUNT_MUST_BE_POSITIVE = "Valor de baixa deve ser maior que zero.";
    public static final String PAYMENT_AMOUNT_EXCEEDS_PAYABLE =
            "Valor de baixa não pode ser maior que o valor da conta.";
    public static final String PAID_PAYABLE_CANNOT_BE_INSTALLMENTED =
            "Não é possível parcelar conta já paga.";
    public static final String SUPPLIER_NOT_FOUND = "Fornecedor não encontrado. Id: ";
    public static final String EMPLOYEE_NOT_FOUND = "Funcionário não encontrado. Id: ";
    public static final String PAYMENT_METHOD_NOT_FOUND = "Forma de pagamento não encontrada. Id: ";
    public static final String PAYMENT_FREQUENCY_NOT_FOUND = "Frequência não encontrada. Id: ";
    public static final String FILE_NOT_FOUND = "Arquivo não encontrado. Id: ";
    public static final String FILE_DOES_NOT_BELONG_TO_PAYABLE = "Arquivo não pertence à conta informada.";
    public static final String ID_COMPLEMENT = " Id: ";

    private PayableErrorMessages() {
    }
}
