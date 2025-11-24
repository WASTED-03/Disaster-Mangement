package com.example.disastermanagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket configuration for real-time notifications.
 * Enables STOMP messaging protocol for bidirectional communication.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configure the message broker.
     * Enables a simple in-memory message broker to carry messages back to the client
     * on destinations prefixed with "/topic".
     */
    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry config) {
        // Enable a simple in-memory message broker to carry messages to clients
        // on destinations prefixed with "/topic"
        config.enableSimpleBroker("/topic");
        
        // Prefix for messages bound to methods annotated with @MessageMapping
        // Clients will send messages to destinations prefixed with "/app"
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Register STOMP endpoints.
     * Clients will connect to "/ws" endpoint to establish WebSocket connection.
     */
    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        // Register "/ws" endpoint for WebSocket connections
        // Clients will connect to: ws://localhost:8080/ws
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Allow all origins (configure appropriately for production)
                .withSockJS(); // Enable SockJS fallback options for browsers that don't support WebSocket
    }
}

