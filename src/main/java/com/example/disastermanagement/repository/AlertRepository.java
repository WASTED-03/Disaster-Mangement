package com.example.disastermanagement.repository;

import com.example.disastermanagement.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findTop20ByOrderByTimestampDesc();
}

