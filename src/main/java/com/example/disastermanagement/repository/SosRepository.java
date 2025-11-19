package com.example.disastermanagement.repository;

import com.example.disastermanagement.model.SosRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface SosRepository extends JpaRepository<SosRequest, Long> {

    List<SosRequest> findByUserEmail(String email);

    List<SosRequest> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
