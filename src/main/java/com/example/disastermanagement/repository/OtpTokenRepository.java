package com.example.disastermanagement.repository;

import com.example.disastermanagement.model.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    Optional<OtpToken> findFirstByEmailAndUsedFalseOrderByExpiresAtDesc(String email);
}
