package com.example.disastermanagement.service;

import com.example.disastermanagement.model.OtpToken;
import com.example.disastermanagement.repository.OtpTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    private final OtpTokenRepository otpRepo;
    private final EmailService emailService;
    private final Random random = new Random();

    public OtpService(OtpTokenRepository otpRepo, EmailService emailService) {
        this.otpRepo = otpRepo;
        this.emailService = emailService;
    }

    // Generate OTP + delete old tokens + send email
    @Transactional
    public String generateAndSendOtp(String email) {

        // Clean any previous OTPs for this email
        otpRepo.deleteAllByEmail(email);

        // Generate 6-digit OTP
        String otp = String.format("%06d", random.nextInt(1_000_000));

        // Save OTP in DB
        OtpToken token = OtpToken.builder()
                .email(email)
                .otp(otp)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .used(false)
                .build();

        otpRepo.save(token);

        // Send email
        emailService.sendOtpEmail(email, otp);

        return otp;
    }

    // Verify OTP
    public boolean verifyOtp(String email, String otp) {

        var opt = otpRepo.findTopByEmailAndUsedFalseOrderByExpiresAtDesc(email);

        if (opt.isEmpty())
            return false;

        OtpToken token = opt.get();

        // Check expiration
        if (token.getExpiresAt().isBefore(LocalDateTime.now()))
            return false;

        // Check OTP match
        if (!token.getOtp().equals(otp))
            return false;

        // Mark as used
        token.setUsed(true);
        otpRepo.save(token);

        return true;
    }
}
