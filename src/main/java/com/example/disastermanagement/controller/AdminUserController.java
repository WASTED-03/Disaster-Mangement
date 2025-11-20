package com.example.disastermanagement.controller;

import com.example.disastermanagement.model.User;
import com.example.disastermanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<User> listUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/promote")
    public Map<String, String> promoteToAdmin(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return Map.of("error", "User not found");
        }

        user.addRole("ADMIN");
        user.setVerified(true);
        userRepository.save(user);

        return Map.of("message", "User promoted to ADMIN successfully");
    }

    @PatchMapping("/{id}/disable")
    public User disable(@PathVariable long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setEnabled(false);
        return userRepository.save(user);
    }

    @PatchMapping("/{id}/enable")
    public User enable(@PathVariable long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setEnabled(true);
        return userRepository.save(user);
    }

    @PatchMapping("/{id}/demote")
    public User demote(@PathVariable long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.removeRole("ADMIN");
        return userRepository.save(user);
    }

    @PatchMapping("/{id}/verify")
    public User forceVerify(@PathVariable long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setVerified(true);
        return userRepository.save(user);
    }
}
