package com.example.disastermanagement.controller;

import com.example.disastermanagement.model.User;
import com.example.disastermanagement.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/admin")
public class AdminUserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/promote")
    public ResponseEntity<Map<String, String>> promoteToAdmin(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        return userRepository.findByEmail(email)
                .map(user -> {
                    Set<String> existingRoles = user.getRoles() == null ? new HashSet<>() : user.getRoles();
                    Set<String> updatedRoles = new HashSet<>(existingRoles);
                    updatedRoles.add("ADMIN");
                    user.setRoles(updatedRoles);
                    userRepository.save(user);
                    return ResponseEntity.ok(Map.of("message", "User promoted to ADMIN successfully"));
                })
                .orElseGet(() -> ResponseEntity.badRequest().body(Map.of("error", "User not found")));
    }

    @PostMapping("/create")
    @SuppressWarnings("DataFlowIssue")
    public ResponseEntity<Map<String, String>> createAdmin(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and password are required"));
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "User already exists"));
        }

        Set<String> roles = new HashSet<>();
        roles.add("ADMIN");

        User admin = new User();
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRoles(roles);
        admin.setVerified(true);

        userRepository.save(admin);
        return ResponseEntity.ok(Map.of("message", "Admin account created successfully"));
    }
}
