package com.example.disastermanagement.controller;

import com.example.disastermanagement.model.RescueTeam;
import com.example.disastermanagement.service.RescueTeamService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/rescue-teams")
public class AdminRescueTeamController {

    private final RescueTeamService rescueTeamService;

    public AdminRescueTeamController(RescueTeamService rescueTeamService) {
        this.rescueTeamService = rescueTeamService;
    }

    @GetMapping
    public List<RescueTeam> listAll() {
        return rescueTeamService.listTeams();
    }

    @GetMapping("/available")
    public List<RescueTeam> available() {
        return rescueTeamService.availableTeams();
    }

    @PostMapping
    public RescueTeam create(@RequestBody RescueTeam team) {
        return rescueTeamService.createTeam(team);
    }

    @PatchMapping("/{id}/availability")
    public RescueTeam updateAvailability(@PathVariable Long id, @RequestParam boolean available) {
        return rescueTeamService.updateAvailability(id, available);
    }
}


