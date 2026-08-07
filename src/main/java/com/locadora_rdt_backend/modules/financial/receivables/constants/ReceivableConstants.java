package com.locadora_rdt_backend.modules.financial.receivables.constants;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class ReceivableConstants {

    public static final BigDecimal ZERO = BigDecimal.ZERO;
    public static final BigDecimal FILTER_AMOUNT_DISABLED = BigDecimal.valueOf(-1);
    public static final LocalDate FILTER_DATE_DISABLED = LocalDate.of(1970, 1, 1);
    public static final long FILTER_ID_DISABLED = -1L;
    public static final BigDecimal PERCENT_DIVISOR = BigDecimal.valueOf(100);
    public static final int MONEY_SCALE = 2;

    public static final String STATUS_ALL = "ALL";
    public static final String STATUS_OPEN = "OPEN";
    public static final String PERIOD_DUE = "DUE";
    public static final String PERIOD_PAYMENT = "PAYMENT";
    public static final String PERIOD_CREATED = "CREATED";
    public static final String PERIOD_DUE_DATE = "DUE_DATE";
    public static final String PERIOD_PAYMENT_DATE = "PAYMENT_DATE";
    public static final String PERIOD_CREATED_DATE = "CREATED_DATE";
    public static final String ORDER_BY_DUE_DATE = "dueDate";
    public static final String ORDER_BY_PAYMENT_DATE = "paymentDate";
    public static final String ORDER_BY_CREATED_DATE = "createdDate";
    public static final String ORDER_BY_AMOUNT = "amount";
    public static final String ORDER_BY_DESCRIPTION = "description";
    public static final String DIRECTION_ASC = "ASC";
    public static final String DIRECTION_DESC = "DESC";

    public static final String CASH_PAYMENT_FREQUENCY = "À vista";
    public static final String RENTAL_REFERENCE = "RENTAL";
    public static final String RENTAL_DESCRIPTION_PREFIX = "Locação ";
    public static final String RENTAL_NOTE = "Conta gerada automaticamente na baixa da locação.";
    public static final String DEFAULT_DESCRIPTION = "Conta a receber";

    private ReceivableConstants() {
    }
}
