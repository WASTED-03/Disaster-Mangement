package com.example.disastermanagement.controller;

import com.example.disastermanagement.model.User;
import com.example.disastermanagement.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;


@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepo;

    public AdminController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @PostMapping("/promote")
    public Map<String, String> promoteToAdmin(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.addRole("ADMIN");
        user.setVerified(true);

        userRepo.save(user);

        return Map.of("status", "success", "message", "User promoted to ADMIN");
    }
}
