package com.example.disastermanagement.service;

import com.example.disastermanagement.model.SosRequest;
import com.example.disastermanagement.repository.SosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SosService {

    @Autowired
    private SosRepository sosRepository;

    public SosRequest createSos(SosRequest sos) {
        sos.setTimestamp(LocalDateTime.now());
        return sosRepository.save(sos);
    }
    public List<SosRequest> getAllSos() {
    return sosRepository.findAll();
}

public List<SosRequest> getByEmail(String email) {
    return sosRepository.findByEmail(email);
}

public List<SosRequest> getByDate(String date) {
    LocalDateTime start = LocalDateTime.parse(date + "T00:00:00");
    LocalDateTime end = LocalDateTime.parse(date + "T23:59:59");
    return sosRepository.findByTimestampBetween(start, end);
}

}
