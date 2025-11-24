package com.example.disastermanagement.config;

import java.security.Principal;
import java.util.List;

/**
 * Custom Principal implementation for WebSocket authentication.
 * Stores user email and roles for authorization checks.
 */
public class WebSocketPrincipal implements Principal {

    private final String name;
    private final List<String> roles;

    public WebSocketPrincipal(String name, List<String> roles) {
        this.name = name;
        this.roles = roles != null ? roles : List.of();
    }

    @Override
    public String getName() {
        return name;
    }

    public List<String> getRoles() {
        return roles;
    }

    public boolean hasRole(String role) {
        return roles.contains(role);
    }
}

