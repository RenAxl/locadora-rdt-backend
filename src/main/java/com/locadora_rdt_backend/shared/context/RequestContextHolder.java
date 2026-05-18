package com.locadora_rdt_backend.shared.context;

import java.util.Optional;

public final class RequestContextHolder {

    private static final ThreadLocal<RequestContext> CONTEXT = new ThreadLocal<>();

    private RequestContextHolder() {
    }

    public static void set(RequestContext context) {
        CONTEXT.set(context);
    }

    public static Optional<RequestContext> get() {
        return Optional.ofNullable(CONTEXT.get());
    }

    public static RequestContext getRequired() {
        return get().orElseThrow(() ->
                new IllegalStateException("RequestContext não encontrado para a thread atual")
        );
    }

    public static RequestContext getOrCreate() {
        RequestContext context = CONTEXT.get();

        if (context == null) {
            context = new RequestContext();
            CONTEXT.set(context);
        }

        return context;
    }

    public static void clear() {
        CONTEXT.remove();
    }
}