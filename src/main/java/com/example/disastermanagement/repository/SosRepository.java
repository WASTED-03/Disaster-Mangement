package com.example.disastermanagement.repository;

import com.example.disastermanagement.model.SosRequest;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SosRepository extends JpaRepository<SosRequest, Long> {
    List<SosRequest> findByEmail(String email);
    List<SosRequest> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
