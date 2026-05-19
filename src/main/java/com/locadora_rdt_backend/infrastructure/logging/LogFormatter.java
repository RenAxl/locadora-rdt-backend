package com.locadora_rdt_backend.infrastructure.logging;

public interface LogFormatter {

    String format(LogEvent event);
}
