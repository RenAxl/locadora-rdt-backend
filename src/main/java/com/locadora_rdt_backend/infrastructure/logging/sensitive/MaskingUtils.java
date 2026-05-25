package com.locadora_rdt_backend.infrastructure.logging.sensitive;

public final class MaskingUtils {

    private MaskingUtils() {
    }

    public static String mask(String value) {

        if (value == null || value.isBlank()) {
            return value;
        }

        return "******";
    }

    public static String maskCpf(String cpf) {

        if (cpf == null || cpf.length() < 11) {
            return "***";
        }

        return cpf.substring(0, 3)
                + ".***.***-"
                + cpf.substring(9);
    }

    public static String maskEmail(String email) {

        if (email == null || !email.contains("@")) {
            return "***";
        }

        String[] parts = email.split("@");

        return parts[0].charAt(0)
                + "***@"
                + parts[1];
    }
}