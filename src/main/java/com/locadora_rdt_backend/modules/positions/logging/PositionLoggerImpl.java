package com.locadora_rdt_backend.modules.positions.logging;

import com.locadora_rdt_backend.infrastructure.logging.ApplicationLogger;
import org.springframework.stereotype.Component;

@Component
public class PositionLoggerImpl implements PositionLogger {

    private static final String CLASS_NAME = "PositionServiceImpl";
    private static final String RESOURCE = "positions";

    private static final int HTTP_STATUS_OK = 200;
    private static final int HTTP_STATUS_CREATED = 201;
    private static final int HTTP_STATUS_NO_CONTENT = 204;
    private static final int HTTP_STATUS_CONFLICT = 409;

    private final ApplicationLogger logger;
    private final PositionLogEventFactory eventFactory;

    public PositionLoggerImpl(
            ApplicationLogger logger,
            PositionLogEventFactory eventFactory
    ) {
        this.logger = logger;
        this.eventFactory = eventFactory;
    }

    @Override
    public void logSearchStarted(String name) {
        PositionLogContext context = createContext(
                PositionLogOperations.SEARCH,
                PositionLogStatus.STARTED,
                "findAllPaged"
        );

        context.setFilterName(name);
        context.setSuccess(null);

        logger.log(eventFactory.createInfo(
                PositionLogMessages.POSITION_SEARCH_STARTED,
                context
        ));
    }

    @Override
    public void logSearchFinished(String name) {
        PositionLogContext context = createContext(
                PositionLogOperations.SEARCH,
                PositionLogStatus.SUCCESS,
                "findAllPaged"
        );

        context.setFilterName(name);
        context.setSuccess(true);
        context.setHttpStatus(HTTP_STATUS_OK);

        logger.log(eventFactory.createInfo(
                PositionLogMessages.POSITION_SEARCH_FINISHED,
                context
        ));
    }

    @Override
    public void logDetailsStarted(Long id) {
        PositionLogContext context = createContext(
                PositionLogOperations.FIND_BY_ID,
                PositionLogStatus.STARTED,
                "findById"
        );

        context.setPositionId(id);
        context.setSuccess(null);

        logger.log(eventFactory.createInfo(
                PositionLogMessages.POSITION_DETAILS_STARTED,
                context
        ));
    }

    @Override
    public void logDetailsFinished(Long id) {
        PositionLogContext context = createContext(
                PositionLogOperations.FIND_BY_ID,
                PositionLogStatus.SUCCESS,
                "findById"
        );

        context.setPositionId(id);
        context.setSuccess(true);
        context.setHttpStatus(HTTP_STATUS_OK);

        logger.log(eventFactory.createInfo(
                PositionLogMessages.POSITION_DETAILS_FINISHED,
                context
        ));
    }

    @Override
    public void logCreated(Long id, String name) {
        PositionLogContext context = createContext(
                PositionLogOperations.CREATE,
                PositionLogStatus.SUCCESS,
                "insert"
        );

        context.setPositionId(id);
        context.setPositionName(name);
        context.setSuccess(true);
        context.setHttpStatus(HTTP_STATUS_CREATED);

        logger.log(eventFactory.createInfo(
                PositionLogMessages.POSITION_CREATED,
                context
        ));
    }

    @Override
    public void logUpdated(Long id, String name) {
        PositionLogContext context = createContext(
                PositionLogOperations.UPDATE,
                PositionLogStatus.SUCCESS,
                "update"
        );

        context.setPositionId(id);
        context.setPositionName(name);
        context.setSuccess(true);
        context.setHttpStatus(HTTP_STATUS_OK);

        logger.log(eventFactory.createInfo(
                PositionLogMessages.POSITION_UPDATED,
                context
        ));
    }

    @Override
    public void logDeleted(Long id) {
        PositionLogContext context = createContext(
                PositionLogOperations.DELETE,
                PositionLogStatus.SUCCESS,
                "delete"
        );

        context.setPositionId(id);
        context.setSuccess(true);
        context.setHttpStatus(HTTP_STATUS_NO_CONTENT);

        logger.log(eventFactory.createInfo(
                PositionLogMessages.POSITION_DELETED,
                context
        ));
    }

    @Override
    public void logDeleteFailed(Long id, Exception exception) {
        PositionLogContext context = createContext(
                PositionLogOperations.DELETE,
                PositionLogStatus.ERROR,
                "delete"
        );

        context.setPositionId(id);
        context.setSuccess(false);
        context.setHttpStatus(HTTP_STATUS_CONFLICT);
        context.setErrorCode("POSITION_DELETE_FAILED");

        if (exception != null) {
            context.setExceptionName(exception.getClass().getSimpleName());
        }

        logger.log(eventFactory.createError(
                PositionLogMessages.POSITION_DELETE_FAILED,
                context,
                exception
        ));
    }

    private PositionLogContext createContext(
            String operation,
            String status,
            String methodName
    ) {
        PositionLogContext context = new PositionLogContext();

        context.setResource(RESOURCE);
        context.setOperation(operation);
        context.setStatus(status);
        context.setClassName(CLASS_NAME);
        context.setMethodName(methodName);

        return context;
    }
}