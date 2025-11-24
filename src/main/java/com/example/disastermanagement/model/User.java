package com.example.disastermanagement.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    // Roles stored as comma-separated string, e.g. "USER,ADMIN"
    @Column(length = 500)
    private String roles;

    @Builder.Default
    private boolean enabled = true;
    @Builder.Default
    private boolean verified = false;

    // Location fields for location-based alerts
    private Double latitude;
    private Double longitude;

    public Set<String> getRoleSet() {
        if (roles == null || roles.isBlank()) return Collections.emptySet();
        return Arrays.stream(roles.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
    }

    public void addRole(String role) {
        Set<String> s = getRoleSet();
        s.add(role);
        this.roles = String.join(",", s);
    }

    public void removeRole(String role) {
        Set<String> s = getRoleSet();
        s.remove(role);
        this.roles = String.join(",", s);
    }
}
