package com.locadora_rdt_backend.tests.shared.context;

import com.locadora_rdt_backend.shared.context.RequestContext;
import com.locadora_rdt_backend.shared.context.RequestContextFactory;
import com.locadora_rdt_backend.shared.context.RequestContextHolder;
import com.locadora_rdt_backend.shared.context.ThreadLocalRequestContextProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

class RequestContextTests {

    @AfterEach
    void tearDown() {
        RequestContextHolder.clear();
    }

    @Test
    void requestContextShouldStoreAttributesAndFinishRequest() {
        RequestContext context = new RequestContext();
        Instant startedAt = Instant.parse("2026-01-01T10:00:00Z");
        context.setStartedAt(startedAt);

        context.addAttribute("key", "value");
        context.addAttribute(null, "ignored");
        context.addAttribute(" ", "ignored");
        context.addAttribute("nullValue", null);
        context.finish(200, "SUCCESS");

        Assertions.assertEquals("value", context.getAttribute("key"));
        Assertions.assertEquals(1, context.getAttributes().size());
        Assertions.assertEquals(200, context.getHttpStatus());
        Assertions.assertEquals("SUCCESS", context.getOutcome());
        Assertions.assertNotNull(context.getEndedAt());
        Assertions.assertTrue(context.getDurationMs() >= 0);
        Assertions.assertTrue(context.getDurationNs() >= 0);
    }

    @Test
    void requestContextShouldExposeAllProperties() {
        RequestContext context = new RequestContext();
        Instant startedAt = Instant.parse("2026-01-01T10:00:00Z");
        Instant endedAt = Instant.parse("2026-01-01T10:00:01Z");

        context.setRequestId("request-id");
        context.setCorrelationId("correlation-id");
        context.setTraceId("trace-id");
        context.setParentTraceId("parent-trace-id");
        context.setParentSpanId("parent-span-id");
        context.setUsername("admin");
        context.setUserId("1");
        context.setSessionId("session-id");
        context.setMethod("GET");
        context.setPath("/users");
        context.setRequestUri("/users?page=0");
        context.setQueryString("page=0");
        context.setClientIp("127.0.0.1");
        context.setUserAgent("JUnit");
        context.setHttpStatus(201);
        context.setOutcome("CREATED");
        context.setStartedAt(startedAt);
        context.setEndedAt(endedAt);
        context.setDurationMs(1000L);
        context.setDurationNs(1_000_000_000L);

        Assertions.assertEquals("request-id", context.getRequestId());
        Assertions.assertEquals("correlation-id", context.getCorrelationId());
        Assertions.assertEquals("trace-id", context.getTraceId());
        Assertions.assertEquals("parent-trace-id", context.getParentTraceId());
        Assertions.assertEquals("parent-span-id", context.getParentSpanId());
        Assertions.assertEquals("admin", context.getUsername());
        Assertions.assertEquals("1", context.getUserId());
        Assertions.assertEquals("session-id", context.getSessionId());
        Assertions.assertEquals("GET", context.getMethod());
        Assertions.assertEquals("/users", context.getPath());
        Assertions.assertEquals("/users?page=0", context.getRequestUri());
        Assertions.assertEquals("page=0", context.getQueryString());
        Assertions.assertEquals("127.0.0.1", context.getClientIp());
        Assertions.assertEquals("JUnit", context.getUserAgent());
        Assertions.assertEquals(201, context.getHttpStatus());
        Assertions.assertEquals("CREATED", context.getOutcome());
        Assertions.assertEquals(startedAt, context.getStartedAt());
        Assertions.assertEquals(endedAt, context.getEndedAt());
        Assertions.assertEquals(1000L, context.getDurationMs());
        Assertions.assertEquals(1_000_000_000L, context.getDurationNs());
    }

    @Test
    void staticHolderShouldStoreCreateAndClearContext() {
        Assertions.assertTrue(RequestContextHolder.get().isEmpty());
        Assertions.assertThrows(IllegalStateException.class, RequestContextHolder::getRequired);

        RequestContext created = RequestContextHolder.getOrCreate();

        Assertions.assertSame(created, RequestContextHolder.getRequired());
        Assertions.assertSame(created, RequestContextHolder.getOrCreate());

        RequestContext explicit = new RequestContext();
        RequestContextHolder.set(explicit);

        Assertions.assertSame(explicit, RequestContextHolder.getRequired());

        RequestContextHolder.clear();

        Assertions.assertTrue(RequestContextHolder.get().isEmpty());
    }

    @Test
    void providerShouldStoreCreateAndClearContext() {
        ThreadLocalRequestContextProvider provider = new ThreadLocalRequestContextProvider();

        Assertions.assertTrue(provider.get().isEmpty());
        Assertions.assertThrows(IllegalStateException.class, provider::getRequired);

        RequestContext created = provider.getOrCreate();

        Assertions.assertSame(created, provider.getRequired());
        Assertions.assertSame(created, provider.getOrCreate());

        RequestContext explicit = new RequestContext();
        provider.set(explicit);

        Assertions.assertSame(explicit, provider.getRequired());

        provider.clear();

        Assertions.assertTrue(provider.get().isEmpty());
    }

    @Test
    void factoryShouldCreateAndFinishContext() {
        RequestContextFactory factory = new RequestContextFactory();

        RequestContext context = factory.create(
                "correlation-id",
                "trace-id",
                "admin",
                "POST",
                "/customers",
                "127.0.0.1",
                "JUnit"
        );

        Assertions.assertEquals("correlation-id", context.getCorrelationId());
        Assertions.assertEquals("trace-id", context.getTraceId());
        Assertions.assertEquals("admin", context.getUsername());
        Assertions.assertEquals("POST", context.getMethod());
        Assertions.assertEquals("/customers", context.getPath());
        Assertions.assertEquals("127.0.0.1", context.getClientIp());
        Assertions.assertEquals("JUnit", context.getUserAgent());
        Assertions.assertNotNull(context.getStartedAt());

        factory.finish(context);

        Assertions.assertNotNull(context.getEndedAt());
        Assertions.assertTrue(context.getDurationMs() >= 0);
    }

    @Test
    void factoryShouldFinishContextWithoutStartedAt() {
        RequestContextFactory factory = new RequestContextFactory();
        RequestContext context = new RequestContext();
        context.setStartedAt(null);

        factory.finish(context);

        Assertions.assertNotNull(context.getEndedAt());
        Assertions.assertNull(context.getDurationMs());
    }
}
