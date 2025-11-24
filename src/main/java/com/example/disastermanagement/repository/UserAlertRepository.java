package com.example.disastermanagement.repository;

import com.example.disastermanagement.model.UserAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for UserAlert entity.
 */
public interface UserAlertRepository extends JpaRepository<UserAlert, Long> {
    
    /**
     * Find all alerts for a specific user, ordered by timestamp descending.
     * 
     * @param userEmail The email of the user
     * @return List of alerts for the user, ordered by timestamp descending
     */
    List<UserAlert> findByUserEmailOrderByTimestampDesc(String userEmail);
    
    /**
     * Find all alerts for a specific user after a given timestamp, ordered by timestamp descending.
     * 
     * @param userEmail The email of the user
     * @param time The timestamp to search after
     * @return List of alerts for the user after the given time, ordered by timestamp descending
     */
    List<UserAlert> findByUserEmailAndTimestampAfterOrderByTimestampDesc(String userEmail, LocalDateTime time);
    
    /**
     * Find all alerts after a specific timestamp, ordered by timestamp descending.
     * 
     * @param time The timestamp to search after
     * @return List of alerts after the given time, ordered by timestamp descending
     */
    List<UserAlert> findByTimestampAfterOrderByTimestampDesc(LocalDateTime time);
}

