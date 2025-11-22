package com.example.disastermanagement.controller;

import com.example.disastermanagement.model.ResourceCenter;
import com.example.disastermanagement.service.ResourceCenterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/resources")
public class AdminResourceController {

    private final ResourceCenterService resourceService;

    public AdminResourceController(ResourceCenterService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createResource(@RequestBody ResourceCenter resource) {
        ResourceCenter created = resourceService.createResource(resource);
        return ResponseEntity.ok(Map.of(
                "message", "Resource center created successfully",
                "resourceId", created.getId()
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateResource(
            @PathVariable Long id,
            @RequestBody ResourceCenter updates) {
        ResourceCenter updated = resourceService.updateResource(id, updates);
        return ResponseEntity.ok(Map.of(
                "message", "Resource center updated successfully",
                "resourceId", updated.getId()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return ResponseEntity.ok(Map.of("message", "Resource center deleted successfully"));
    }
}

