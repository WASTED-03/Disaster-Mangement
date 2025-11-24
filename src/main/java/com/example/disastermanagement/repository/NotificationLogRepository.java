package com.example.disastermanagement.repository;

import com.example.disastermanagement.model.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for NotificationLog entity.
 */
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    
    /**
     * Find all notification logs, ordered by timestamp descending.
     * 
     * @return List of all notification logs
     */
    List<NotificationLog> findAllByOrderByTimestampDesc();
    
    /**
     * Find notification logs for a specific recipient email, ordered by timestamp descending.
     * 
     * @param email The recipient email
     * @return List of notification logs for the recipient
     */
    List<NotificationLog> findByRecipientEmailOrderByTimestampDesc(String email);
    
    /**
     * Find notification logs by type, ordered by timestamp descending.
     * 
     * @param type The notification type (USER, ADMIN, BROADCAST)
     * @return List of notification logs of the specified type
     */
    List<NotificationLog> findByNotificationTypeOrderByTimestampDesc(String type);
    
    /**
     * Find notification logs after a specific timestamp, ordered by timestamp descending.
     * 
     * @param timestamp The timestamp to search after
     * @return List of notification logs after the given time
     */
    List<NotificationLog> findByTimestampAfterOrderByTimestampDesc(LocalDateTime timestamp);
}

