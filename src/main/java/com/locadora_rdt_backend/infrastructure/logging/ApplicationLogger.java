package com.locadora_rdt_backend.infrastructure.logging;

public interface ApplicationLogger {

    void trace(String message);

    void debug(String message);

    void info(String message);

    void warn(String message);

    void error(String message);

    void error(String message, Throwable exception);

    void log(LogEvent event);
}
