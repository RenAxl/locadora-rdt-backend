package com.locadora_rdt_backend.infrastructure.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ApplicationLoggerImpl implements ApplicationLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationLoggerImpl.class);

    private final LogEventFactory logEventFactory;
    private final LogFormatter logFormatter;

    public ApplicationLoggerImpl(
            LogEventFactory logEventFactory,
            LogFormatter logFormatter
    ) {
        this.logEventFactory = logEventFactory;
        this.logFormatter = logFormatter;
    }

    @Override
    public void trace(String message) {
        log(logEventFactory.create(LogLevel.TRACE, message));
    }

    @Override
    public void debug(String message) {
        log(logEventFactory.create(LogLevel.DEBUG, message));
    }

    @Override
    public void info(String message) {
        log(logEventFactory.create(LogLevel.INFO, message));
    }

    @Override
    public void warn(String message) {
        log(logEventFactory.create(LogLevel.WARN, message));
    }

    @Override
    public void error(String message) {
        log(logEventFactory.create(LogLevel.ERROR, message));
    }

    @Override
    public void error(String message, Throwable exception) {
        log(logEventFactory.createError(message, exception));
    }

    @Override
    public void log(LogEvent event) {
        String formattedMessage = logFormatter.format(event);

        if (LogLevel.TRACE.equals(event.getLevel())) {
            LOGGER.trace(formattedMessage);
            return;
        }

        if (LogLevel.DEBUG.equals(event.getLevel())) {
            LOGGER.debug(formattedMessage);
            return;
        }

        if (LogLevel.INFO.equals(event.getLevel())) {
            LOGGER.info(formattedMessage);
            return;
        }

        if (LogLevel.WARN.equals(event.getLevel())) {
            LOGGER.warn(formattedMessage);
            return;
        }

        LOGGER.error(formattedMessage);
    }
}