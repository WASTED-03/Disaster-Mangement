package com.example.disastermanagement.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * WebSocket interceptor for JWT authentication and authorization.
 * Validates JWT tokens during WebSocket handshake and subscription.
 */
@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor, ChannelInterceptor {

    private final JwtUtil jwtUtil;

    public WebSocketAuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Intercept WebSocket handshake to extract and validate JWT token.
     * Token can be passed as query parameter: ?token=xxx
     */
    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes) throws Exception {
        
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpServletRequest httpRequest = servletRequest.getServletRequest();
            
            // Extract token from query parameter
            String token = httpRequest.getParameter("token");
            
            if (token == null || token.isBlank()) {
                // Try to extract from Authorization header
                String authHeader = httpRequest.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                }
            }
            
            if (token != null && !token.isBlank()) {
                try {
                    // Validate token and extract user info
                    String email = jwtUtil.extractUsername(token);
                    List<String> roles = jwtUtil.extractRoles(token);
                    
                    if (email != null && jwtUtil.validateToken(token, email)) {
                        // Store user info in session attributes
                        attributes.put("userEmail", email);
                        attributes.put("userRoles", roles);
                        attributes.put("jwtToken", token);
                        
                        // Create and store Principal for authorization
                        WebSocketPrincipal principal = new WebSocketPrincipal(email, roles);
                        attributes.put("principal", principal);
                        
                        return true;
                    }
                } catch (Exception e) {
                    System.err.println("WebSocket JWT validation failed: " + e.getMessage());
                    return false;
                }
            }
            
            // Reject connection without valid token
            System.err.println("WebSocket connection rejected: No valid JWT token");
            return false;
        }
        
        return false;
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
                               @NonNull WebSocketHandler wsHandler, @Nullable Exception exception) {
        // No action needed after handshake
    }

    /**
     * Intercept STOMP messages to authorize subscriptions.
     * Sets authentication from session attributes and validates subscription permissions.
     * Ensures only admins can subscribe to /topic/admins.
     * Ensures users can only subscribe to their own /topic/user/{email}.
     */
    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null) {
            // Set Principal from session attributes if not already set
            if (accessor.getUser() == null) {
                Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
                if (sessionAttributes != null) {
                    WebSocketPrincipal principal = (WebSocketPrincipal) sessionAttributes.get("principal");
                    if (principal != null) {
                        accessor.setUser(principal);
                    }
                }
            }
            
            // Authorize subscriptions
            if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                String destination = accessor.getDestination();
                Principal user = accessor.getUser();
                
                if (destination == null) {
                    return null; // Reject subscription without destination
                }
                
                // Get user email and roles
                String userEmail = null;
                List<String> userRoles = new ArrayList<>();
                
                if (user instanceof WebSocketPrincipal) {
                    WebSocketPrincipal principal = (WebSocketPrincipal) user;
                    userEmail = principal.getName();
                    userRoles = principal.getRoles();
                } else if (user instanceof UsernamePasswordAuthenticationToken) {
                    UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken) user;
                    userEmail = authToken.getName();
                    userRoles = authToken.getAuthorities().stream()
                            .map(a -> a.getAuthority())
                            .collect(Collectors.toList());
                }
                
                // Check authorization based on destination
                if (destination.startsWith("/topic/admins")) {
                    // Only admins can subscribe to admin topics
                    if (userRoles == null || !userRoles.contains("ADMIN")) {
                        System.err.println("Subscription rejected: User " + userEmail + " attempted to subscribe to " + destination);
                        return null; // Reject subscription
                    }
                } else if (destination.startsWith("/topic/user/")) {
                    // Users can only subscribe to their own user topic
                    String topicEmail = destination.replace("/topic/user/", "");
                    if (userEmail == null || !userEmail.equals(topicEmail)) {
                        System.err.println("Subscription rejected: User " + userEmail + " attempted to subscribe to " + destination);
                        return null; // Reject subscription
                    }
                }
                // Allow subscription to /topic/global for all authenticated users
            }
        }
        
        return message;
    }
}

