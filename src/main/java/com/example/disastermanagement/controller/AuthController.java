package com.example.disastermanagement.controller;

import com.example.disastermanagement.config.JwtUtil;
import com.example.disastermanagement.model.User;
import com.example.disastermanagement.repository.UserRepository;
import com.example.disastermanagement.service.OtpService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final OtpService otpService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepo,
                          OtpService otpService,
                          JwtUtil jwtUtil,
                          PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.otpService = otpService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String rawPassword = body.get("password");

        if (userRepo.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already registered"));
        }

        User u = User.builder()
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .roles("USER")
                .verified(false)
                .build();

        userRepo.save(u);
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    @PostMapping("/request-otp")
    public ResponseEntity<?> requestOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (userRepo.findByEmail(email).isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        }
        otpService.generateAndSendOtp(email);
        return ResponseEntity.ok(Map.of("status", "OTP sent successfully"));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String otp = body.get("otp");

        boolean ok = otpService.verifyOtp(email, otp);
        if (!ok) return ResponseEntity.badRequest().body(Map.of("error", "Invalid or expired OTP"));

        User user = userRepo.findByEmail(email).get();
        String token = jwtUtil.generateToken(user.getEmail(), user.getRoleSet());
        return ResponseEntity.ok(Map.of("message", "OTP verified successfully", "token", token));
    }
}
