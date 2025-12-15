package com.example.disastermanagement.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

@Component
public class JwtUtil {

    // Use a long random string in production and keep secret in env vars
    private static final String SECRET = "replace_with_a_very_long_random_secret_key_please_change";

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(Base64.getEncoder().encodeToString(SECRET.getBytes()));
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String email, Set<String> roles, String city) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        if (city != null) {
            claims.put("city", city);
        }
        Date now = new Date();
        Date exp = new Date(System.currentTimeMillis() + 3600_000); // 1 hour
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public List<String> extractRolesList(String token) {
        Object rolesObj = parseClaims(token).get("roles");
        if (rolesObj instanceof List) {
            return (List<String>) rolesObj;
        }
        return Collections.emptyList();
    }

    // convenience: returns as List<String>
    public List<String> extractRoles(String token) {
        return extractRolesList(token);
    }

    public String extractCity(String token) {
        return (String) parseClaims(token).get("city");
    }

    public boolean validateToken(String token, String username) {
        try {
            final String sub = extractUsername(token);
            return (sub.equals(username) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        Date exp = parseClaims(token).getExpiration();
        return exp.before(new Date());
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
