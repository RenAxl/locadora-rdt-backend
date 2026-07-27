package com.locadora_rdt_backend.modules.identity.customeraccount.event;

public class CustomerAccountActivationEvent {
    private final String name;
    private final String email;
    private final String token;

    public CustomerAccountActivationEvent(String name, String email, String token) {
        this.name = name;
        this.email = email;
        this.token = token;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getToken() { return token; }
}
