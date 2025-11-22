package com.example.disastermanagement.service;

import com.example.disastermanagement.model.ResourceCenter;
import com.example.disastermanagement.repository.ResourceCenterRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ResourceCenterService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "SHELTER", "HOSPITAL", "FOOD", "WATER", "MEDICAL", 
            "FIRE_STATION", "POLICE_STATION"
    );
    private static final double EARTH_RADIUS_KM = 6371.0;

    private final ResourceCenterRepository repository;

    public ResourceCenterService(ResourceCenterRepository repository) {
        this.repository = repository;
    }

    public ResourceCenter createResource(ResourceCenter resource) {
        validateResource(resource);
        
        if (resource.getTimestamp() == null) {
            resource.setTimestamp(LocalDateTime.now());
        }
        
        return repository.save(resource);
    }

    public List<ResourceCenter> getAllResources() {
        return repository.findAll();
    }

    public List<ResourceCenter> getResourcesByType(String type) {
        if (!StringUtils.hasText(type)) {
            throw new IllegalArgumentException("Resource type is required");
        }
        
        String normalizedType = type.toUpperCase();
        if (!ALLOWED_TYPES.contains(normalizedType)) {
            throw new IllegalArgumentException(
                    "Invalid type. Must be one of: SHELTER, HOSPITAL, FOOD, WATER, MEDICAL, FIRE_STATION, POLICE_STATION"
            );
        }
        
        return repository.findByType(normalizedType);
    }

    public List<ResourceCenter> getResourcesNear(double latitude, double longitude) {
        List<ResourceCenter> allResources = repository.findAll();
        
        return allResources.stream()
                .map(resource -> {
                    double distance = calculateDistance(
                            latitude, longitude,
                            resource.getLatitude(), resource.getLongitude()
                    );
                    // Create a wrapper or use a record to store distance temporarily
                    // For simplicity, we'll sort by distance in the stream
                    return new ResourceWithDistance(resource, distance);
                })
                .sorted(Comparator.comparingDouble(ResourceWithDistance::distance))
                .map(ResourceWithDistance::resource)
                .collect(Collectors.toList());
    }

    public ResourceCenter updateResource(Long id, ResourceCenter updates) {
        ResourceCenter existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Resource center not found"));

        // Update fields if provided
        if (StringUtils.hasText(updates.getName())) {
            existing.setName(updates.getName());
        }
        if (StringUtils.hasText(updates.getType())) {
            String type = updates.getType().toUpperCase();
            if (!ALLOWED_TYPES.contains(type)) {
                throw new IllegalArgumentException("Invalid resource type");
            }
            existing.setType(type);
        }
        if (StringUtils.hasText(updates.getAddress())) {
            existing.setAddress(updates.getAddress());
        }
        if (updates.getLatitude() != 0.0) {
            existing.setLatitude(updates.getLatitude());
        }
        if (updates.getLongitude() != 0.0) {
            existing.setLongitude(updates.getLongitude());
        }
        if (StringUtils.hasText(updates.getContactNumber())) {
            existing.setContactNumber(updates.getContactNumber());
        }
        if (updates.getCapacity() != null) {
            existing.setCapacity(updates.getCapacity());
        }
        if (updates.getCurrentAvailability() != null) {
            existing.setCurrentAvailability(updates.getCurrentAvailability());
        }

        return repository.save(existing);
    }

    public void deleteResource(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Resource center not found");
        }
        repository.deleteById(id);
    }

    private void validateResource(ResourceCenter resource) {
        if (!StringUtils.hasText(resource.getName())) {
            throw new IllegalArgumentException("Name is required");
        }
        if (!StringUtils.hasText(resource.getType())) {
            throw new IllegalArgumentException("Type is required");
        }
        if (!StringUtils.hasText(resource.getAddress())) {
            throw new IllegalArgumentException("Address is required");
        }
        if (!StringUtils.hasText(resource.getContactNumber())) {
            throw new IllegalArgumentException("Contact number is required");
        }

        String type = resource.getType().toUpperCase();
        if (!ALLOWED_TYPES.contains(type)) {
            throw new IllegalArgumentException(
                    "Invalid type. Must be one of: SHELTER, HOSPITAL, FOOD, WATER, MEDICAL, FIRE_STATION, POLICE_STATION"
            );
        }
        resource.setType(type);
    }

    /**
     * Calculate distance between two points using Haversine formula
     * @param lat1 Latitude of first point
     * @param lon1 Longitude of first point
     * @param lat2 Latitude of second point
     * @param lon2 Longitude of second point
     * @return Distance in kilometers
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    // Helper record to store resource with its distance
    private record ResourceWithDistance(ResourceCenter resource, double distance) {}
}

