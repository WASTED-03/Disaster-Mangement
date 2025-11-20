package com.example.disastermanagement.service;

import com.example.disastermanagement.model.RescueTeam;
import com.example.disastermanagement.repository.RescueTeamRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RescueTeamService {

    private final RescueTeamRepository repository;

    public RescueTeamService(RescueTeamRepository repository) {
        this.repository = repository;
    }

    public List<RescueTeam> listTeams() {
        return repository.findAll();
    }

    public List<RescueTeam> availableTeams() {
        return repository.findByAvailableTrue();
    }

    public RescueTeam createTeam(RescueTeam team) {
        if (team == null) {
            throw new IllegalArgumentException("Team payload is required");
        }
        return repository.save(team);
    }

    public RescueTeam updateAvailability(long id, boolean available) {
        RescueTeam team = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rescue team not found"));
        team.setAvailable(available);
        return repository.save(team);
    }

    public RescueTeam getTeam(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rescue team not found"));
    }
}


