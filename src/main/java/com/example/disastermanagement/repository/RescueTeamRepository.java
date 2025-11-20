package com.example.disastermanagement.repository;

import com.example.disastermanagement.model.RescueTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RescueTeamRepository extends JpaRepository<RescueTeam, Long> {
    List<RescueTeam> findByAvailableTrue();
}


