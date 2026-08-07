package com.locadora_rdt_backend.modules.financial.receivables.constants;

import java.awt.Color;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class ReceivableDocumentConstants {

    public static final Locale BRAZIL = new Locale("pt", "BR");
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final Color DARK_BLUE = new Color(13, 42, 77);
    public static final String EMPTY_VALUE = "-";

    private ReceivableDocumentConstants() {
    }
}
