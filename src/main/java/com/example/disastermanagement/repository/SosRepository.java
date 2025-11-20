package com.example.disastermanagement.repository;

import com.example.disastermanagement.model.SosRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface SosRepository extends JpaRepository<SosRequest, Long> {

    List<SosRequest> findByUserEmail(String email);

    List<SosRequest> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<SosRequest> findByUserEmailAndTimestampBetween(String email,
                                                       LocalDateTime start,
                                                       LocalDateTime end);

    long countByStatus(String status);

    long countByTimestampBetween(LocalDateTime start, LocalDateTime end);

    @Query("select s.type as type, count(s) as count from SosRequest s group by s.type")
    List<TypeCount> countByType();

    interface TypeCount {
        String getType();
        long getCount();
    }
}
