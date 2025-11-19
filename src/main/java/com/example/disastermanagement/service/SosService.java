package com.example.disastermanagement.service;

import com.example.disastermanagement.model.SosRequest;
import com.example.disastermanagement.repository.SosRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SosService {

    private final SosRepository repo;

    public SosService(SosRepository repo) {
        this.repo = repo;
    }

    public SosRequest createSos(SosRequest sos) {
        sos.setTimestamp(LocalDateTime.now());
        return repo.save(sos);
    }

    public List<SosRequest> getAll() {
        return repo.findAll();
    }

    public List<SosRequest> getByEmail(String email) {
        return repo.findByUserEmail(email);
    }

    public List<SosRequest> getByDate(LocalDateTime start, LocalDateTime end) {
        return repo.findByTimestampBetween(start, end);
    }
}
