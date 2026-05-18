package com.locadora_rdt_backend.shared.context;

import java.util.Optional;

public interface RequestContextProvider {

    void set(RequestContext context);

    Optional<RequestContext> get();

    RequestContext getRequired();

    RequestContext getOrCreate();

    void clear();
}