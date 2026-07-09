package com.locadora_rdt_backend.shared.reports;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ReportTableSupport {

    public static final BigDecimal ZERO = BigDecimal.ZERO;
    public static final BigDecimal FILTER_AMOUNT_DISABLED = BigDecimal.valueOf(-1);
    public static final LocalDate FILTER_DATE_DISABLED = LocalDate.of(1970, 1, 1);
    public static final long FILTER_ID_DISABLED = -1L;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private ReportTableSupport() {
    }

    public static Map<String, ?> row(String... values) {
        Map<String, String> row = new LinkedHashMap<>();

        for (int i = 0; i < values.length; i++) {
            row.put("column" + i, values[i]);
        }

        return row;
    }

    public static String text(String value) {
        return value == null ? "" : value;
    }

    public static String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return value.trim();
    }

    public static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static String normalizeCode(String value, String defaultValue) {
        if (!hasText(value)) {
            return defaultValue;
        }

        return value.trim().replace("-", "_").toUpperCase();
    }

    public static Long idFilterOrDisabled(Long id) {
        if (id == null || id <= 0) {
            return FILTER_ID_DISABLED;
        }

        return id;
    }

    public static BigDecimal amountFilterOrDisabled(BigDecimal amount) {
        if (amount == null) {
            return FILTER_AMOUNT_DISABLED;
        }

        return amount;
    }

    public static LocalDate dateFilterOrDisabled(LocalDate date) {
        if (date == null) {
            return FILTER_DATE_DISABLED;
        }

        return date;
    }

    public static BigDecimal valueOrZero(BigDecimal value) {
        if (value == null) {
            return ZERO;
        }

        return value;
    }

    public static Integer valueOrZero(Integer value) {
        return value == null ? 0 : value;
    }

    public static String number(Integer value) {
        return String.valueOf(valueOrZero(value));
    }

    public static String date(LocalDate date) {
        if (date == null) {
            return "";
        }

        return date.format(DATE_FORMATTER);
    }

    public static String dateTime(Instant instant) {
        if (instant == null) {
            return "";
        }

        return instant.atZone(ZoneId.systemDefault()).format(DATE_TIME_FORMATTER);
    }

    public static String money(BigDecimal value) {
        return "R$ " + valueOrZero(value).setScale(2, RoundingMode.HALF_UP).toPlainString().replace(".", ",");
    }
}
