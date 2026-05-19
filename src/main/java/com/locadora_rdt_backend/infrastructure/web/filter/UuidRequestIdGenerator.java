package com.locadora_rdt_backend.infrastructure.web.filter;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UuidRequestIdGenerator implements RequestIdGenerator {

    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
