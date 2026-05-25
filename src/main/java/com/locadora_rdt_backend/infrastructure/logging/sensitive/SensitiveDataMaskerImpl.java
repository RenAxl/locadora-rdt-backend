package com.locadora_rdt_backend.infrastructure.logging.sensitive;

import org.springframework.stereotype.Component;

@Component
public class SensitiveDataMaskerImpl
        implements SensitiveDataMasker {

    @Override
    public Object mask(
            String fieldName,
            Object value
    ) {

        if (value == null) {
            return null;
        }

        String normalizedField =
                normalizeFieldName(fieldName);

        String stringValue =
                value.toString();


        if (SensitiveFields.FIELDS.contains(normalizedField)) {

            return MaskingUtils.mask(stringValue);
        }

        if (normalizedField.contains("cpf")) {

            return MaskingUtils.maskCpf(stringValue);
        }

        if (normalizedField.contains("email")) {

            return MaskingUtils.maskEmail(stringValue);
        }

        if (stringValue.matches(
                SensitiveDataPatterns.CPF
        )) {

            return MaskingUtils.maskCpf(stringValue);
        }

        if (stringValue.matches(
                SensitiveDataPatterns.EMAIL
        )) {

            return MaskingUtils.maskEmail(stringValue);
        }

        if (stringValue.matches(
                SensitiveDataPatterns.JWT_TOKEN
        )) {

            return MaskingUtils.mask(stringValue);
        }

        if (stringValue.matches(
                SensitiveDataPatterns.BEARER_TOKEN
        )) {

            return MaskingUtils.mask(stringValue);
        }

        if (normalizedField.matches(
                SensitiveDataPatterns.PASSWORD
        )) {

            return MaskingUtils.mask(stringValue);
        }

        if (normalizedField.matches(
                SensitiveDataPatterns.SECRET
        )) {

            return MaskingUtils.mask(stringValue);
        }

        return value;
    }

    private String normalizeFieldName(
            String fieldName
    ) {

        if (fieldName == null) {
            return "";
        }

        return fieldName
                .trim()
                .toLowerCase();
    }
}