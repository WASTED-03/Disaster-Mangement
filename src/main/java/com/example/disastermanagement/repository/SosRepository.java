package com.example.disastermanagement.repository;

import com.example.disastermanagement.model.SosRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SosRepository extends JpaRepository<SosRequest, Long> {
}
