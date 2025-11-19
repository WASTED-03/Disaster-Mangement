package com.example.disastermanagement.repository;

import com.example.disastermanagement.model.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {

    // Get most recent, unused OTP
    Optional<OtpToken> findTopByEmailAndUsedFalseOrderByExpiresAtDesc(String email);

    // Delete old OTPs for a user
    void deleteAllByEmail(String email);
}
