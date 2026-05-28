package com.locadora_rdt_backend.infrastructure.tracing.context;

import com.locadora_rdt_backend.infrastructure.tracing.model.TraceSpan;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

public final class TraceContextHolder {

    private static final ThreadLocal<TraceContext> CONTEXT = new ThreadLocal<>();

    private static final ThreadLocal<Deque<TraceSpan>> CURRENT_SPANS =
            ThreadLocal.withInitial(ArrayDeque::new);

    private TraceContextHolder() {
    }

    public static void set(TraceContext context) {
        CONTEXT.set(context);
    }

    public static TraceContext get() {
        return CONTEXT.get();
    }

    public static TraceContext getOrCreate() {
        TraceContext context = CONTEXT.get();

        if (context == null) {
            context = new TraceContext();
            CONTEXT.set(context);
        }

        return context;
    }

    public static Optional<TraceSpan> getCurrentSpan() {
        Deque<TraceSpan> spans = CURRENT_SPANS.get();

        if (spans.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(spans.peek());
    }

    public static void push(TraceSpan span) {
        CURRENT_SPANS.get().push(span);

        TraceContext context = getOrCreate();
        context.setCurrentSpanId(span.getSpanId());
        context.setParentSpanId(span.getParentSpanId());
    }

    public static void pop() {
        Deque<TraceSpan> spans = CURRENT_SPANS.get();

        if (!spans.isEmpty()) {
            spans.pop();
        }

        TraceContext context = get();

        if (context != null) {
            if (spans.isEmpty()) {
                context.setCurrentSpanId(null);
                context.setParentSpanId(null);
            } else {
                TraceSpan currentSpan = spans.peek();
                context.setCurrentSpanId(currentSpan.getSpanId());
                context.setParentSpanId(currentSpan.getParentSpanId());
            }
        }

        if (spans.isEmpty()) {
            CURRENT_SPANS.remove();
        }
    }

    public static void clear() {
        CONTEXT.remove();
        CURRENT_SPANS.remove();
    }
}