package com.locadora_rdt_backend.modules.positions.logging;

import com.locadora_rdt_backend.infrastructure.logging.LogEvent;
import com.locadora_rdt_backend.infrastructure.logging.LogEventFactory;
import com.locadora_rdt_backend.infrastructure.logging.LogLevel;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PositionLogEventFactory {

    private static final String DEFAULT_RESOURCE = "positions";

    private final LogEventFactory logEventFactory;

    public PositionLogEventFactory(
            LogEventFactory logEventFactory
    ) {
        this.logEventFactory = logEventFactory;
    }

    public LogEvent createInfo(
            String message,
            PositionLogContext context
    ) {
        LogEvent event = logEventFactory.create(
                LogLevel.INFO,
                message
        );

        fillPositionData(event, context);

        return event;
    }

    public LogEvent createWarn(
            String message,
            PositionLogContext context
    ) {
        LogEvent event = logEventFactory.create(
                LogLevel.WARN,
                message
        );

        fillPositionData(event, context);

        return event;
    }

    public LogEvent createDebug(
            String message,
            PositionLogContext context
    ) {
        LogEvent event = logEventFactory.create(
                LogLevel.DEBUG,
                message
        );

        fillPositionData(event, context);

        return event;
    }

    public LogEvent createError(
            String message,
            PositionLogContext context,
            Throwable exception
    ) {
        LogEvent event = logEventFactory.createError(
                message,
                exception
        );

        fillPositionData(event, context);

        fillErrorData(event, context, exception);

        return event;
    }

    private void fillPositionData(
            LogEvent event,
            PositionLogContext context
    ) {

        if (context == null) {
            event.setResource(DEFAULT_RESOURCE);
            return;
        }

        event.setResource(
                context.getResource() != null
                        ? context.getResource()
                        : DEFAULT_RESOURCE
        );

        event.setOperation(context.getOperation());

        event.setStatus(context.getStatus());

        event.setClassName(context.getClassName());

        event.setMethodName(context.getMethodName());

        event.setDurationMs(context.getDurationMs());

        for (Map.Entry<String, Object> entry :
                context.toAttributes().entrySet()) {

            if (entry.getValue() != null) {
                event.addAttribute(
                        entry.getKey(),
                        entry.getValue()
                );
            }
        }
    }

    private void fillErrorData(
            LogEvent event,
            PositionLogContext context,
            Throwable exception
    ) {

        if (exception == null) {
            return;
        }

        event.setExceptionName(
                exception.getClass().getSimpleName()
        );

        event.setExceptionMessage(
                exception.getMessage()
        );

        if (context != null) {
            context.setSuccess(false);
        }
    }
}