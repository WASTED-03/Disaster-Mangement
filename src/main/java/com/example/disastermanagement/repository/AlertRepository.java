package com.example.disastermanagement.repository;

import com.example.disastermanagement.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AlertRepository extends JpaRepository<Alert, Long>, JpaSpecificationExecutor<Alert> {
    List<Alert> findTop20ByOrderByTimestampDesc();
}
