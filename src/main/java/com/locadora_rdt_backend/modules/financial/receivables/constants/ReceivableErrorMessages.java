package com.locadora_rdt_backend.modules.financial.receivables.constants;

public final class ReceivableErrorMessages {

    public static final String RECEIVABLE_NOT_FOUND = "Conta a receber não encontrada.";
    public static final String DATABASE_INTEGRITY_VIOLATION = "Não foi possível excluir a conta a receber.";
    public static final String RECEIVABLE_ALREADY_INSTALLMENTED = "Conta a receber já foi parcelada.";
    public static final String PAID_RECEIVABLE_CANNOT_BE_UPDATED = "Conta a receber paga não pode ser atualizada.";
    public static final String CANCELED_RECEIVABLE_CANNOT_BE_UPDATED = "Conta a receber cancelada não pode ser atualizada.";
    public static final String CANCELED_RECEIVABLE_CANNOT_BE_PAID = "Conta a receber cancelada não pode receber baixa.";
    public static final String PAID_RECEIVABLE_CANNOT_BE_PAID = "Conta já está paga.";
    public static final String PAYMENT_AMOUNT_MUST_BE_POSITIVE = "Valor de baixa deve ser maior que zero.";
    public static final String PAYMENT_AMOUNT_EXCEEDS_RECEIVABLE =
            "Valor de baixa não pode ser maior que o valor da conta.";
    public static final String PAID_RECEIVABLE_CANNOT_BE_INSTALLMENTED =
            "Não é possível parcelar conta já paga.";
    public static final String CUSTOMER_OR_DESCRIPTION_REQUIRED =
            "Informe o cliente ou a descrição da conta.";
    public static final String CUSTOMER_NOT_FOUND = "Cliente não encontrado. Id: ";
    public static final String PAYMENT_METHOD_NOT_FOUND = "Forma de pagamento não encontrada. Id: ";
    public static final String PAYMENT_FREQUENCY_NOT_FOUND = "Frequência não encontrada. Id: ";
    public static final String CASH_PAYMENT_FREQUENCY_NOT_FOUND =
            "Frequência de pagamento à vista não encontrada.";
    public static final String RECEIPT_ONLY_FOR_PAID_RECEIVABLE =
            "Recibo disponível apenas para contas pagas.";
    public static final String FISCAL_COUPON_ONLY_FOR_PAID_RECEIVABLE =
            "Cupom fiscal disponível apenas para contas pagas.";
    public static final String RECEIPT_GENERATION_ERROR = "Erro ao gerar recibo.";
    public static final String FISCAL_COUPON_GENERATION_ERROR = "Erro ao gerar cupom fiscal.";
    public static final String FILE_NOT_FOUND = "Arquivo não encontrado. Id: ";
    public static final String FILE_DOES_NOT_BELONG_TO_RECEIVABLE =
            "Arquivo não pertence à conta informada.";
    public static final String ID_COMPLEMENT = " Id: ";

    private ReceivableErrorMessages() {
    }
}
