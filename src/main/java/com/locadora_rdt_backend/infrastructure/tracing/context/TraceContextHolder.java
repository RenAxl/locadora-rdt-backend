package com.locadora_rdt_backend.infrastructure.tracing.context;

import com.locadora_rdt_backend.infrastructure.tracing.model.TraceSpan;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

public final class TraceContextHolder {

    private static final ThreadLocal<Deque<TraceSpan>> CURRENT_SPANS =
            ThreadLocal.withInitial(ArrayDeque::new);

    private TraceContextHolder() {
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
    }

    public static void pop() {
        Deque<TraceSpan> spans = CURRENT_SPANS.get();

        if (!spans.isEmpty()) {
            spans.pop();
        }

        if (spans.isEmpty()) {
            CURRENT_SPANS.remove();
        }
    }

    public static void clear() {
        CURRENT_SPANS.remove();
    }
}