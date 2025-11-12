package com.example.disastermanagement.service;

import com.example.disastermanagement.model.OtpToken;
import com.example.disastermanagement.repository.OtpTokenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    private final OtpTokenRepository otpRepo;
    private final EmailService emailService;

    public OtpService(OtpTokenRepository otpRepo, EmailService emailService) {
        this.otpRepo = otpRepo;
        this.emailService = emailService;
    }

    public void generateAndSendOtp(String email) {
        String otp = String.format("%06d", new Random().nextInt(1_000_000));
        OtpToken token = OtpToken.builder()
                .email(email)
                .otp(otp)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .used(false)
                .build();
        otpRepo.save(token);

        emailService.sendSimpleMessage(email, "Your OTP for Disaster Management App", 
                "Your OTP is: " + otp + " (valid for 10 minutes)");
    }

    public boolean verifyOtp(String email, String otp) {
        var opt = otpRepo.findFirstByEmailAndUsedFalseOrderByExpiresAtDesc(email);
        if (opt.isEmpty()) return false;
        OtpToken token = opt.get();
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) return false;
        if (!token.getOtp().equals(otp)) return false;
        token.setUsed(true);
        otpRepo.save(token);
        return true;
    }
}
