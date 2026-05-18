package com.locadora_rdt_backend.shared.context;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ThreadLocalRequestContextProvider implements RequestContextProvider {

    private static final ThreadLocal<RequestContext> CONTEXT = new ThreadLocal<>();

    @Override
    public void set(RequestContext context) {
        CONTEXT.set(context);
    }

    @Override
    public Optional<RequestContext> get() {
        return Optional.ofNullable(CONTEXT.get());
    }

    @Override
    public RequestContext getRequired() {
        return get().orElseThrow(() ->
                new IllegalStateException("RequestContext não encontrado para a thread atual")
        );
    }

    @Override
    public RequestContext getOrCreate() {
        RequestContext context = CONTEXT.get();

        if (context == null) {
            context = new RequestContext();
            CONTEXT.set(context);
        }

        return context;
    }

    @Override
    public void clear() {
        CONTEXT.remove();
    }
}
