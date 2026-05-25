package com.locadora_rdt_backend.infrastructure.logging.sensitive;

public interface SensitiveDataMasker {

    Object mask(
            String fieldName,
            Object value
    );
}