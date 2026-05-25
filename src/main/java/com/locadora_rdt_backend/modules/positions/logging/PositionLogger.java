package com.locadora_rdt_backend.modules.positions.logging;

public interface PositionLogger {

    void logSearchStarted(String name);

    void logSearchFinished(String name);

    void logDetailsStarted(Long id);

    void logDetailsFinished(Long id);

    void logCreated(Long id, String name);

    void logUpdated(Long id, String name);

    void logDeleted(Long id);

    void logDeleteFailed(Long id, Exception exception);
}