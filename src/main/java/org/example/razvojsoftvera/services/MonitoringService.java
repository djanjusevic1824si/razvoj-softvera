package org.example.razvojsoftvera.services;

import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.model.StatusRada;
import org.example.razvojsoftvera.repositories.RadRepository;
import org.example.razvojsoftvera.responses.MonitoringDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@PreAuthorize("hasRole('NASTAVNIK')")
public class MonitoringService {
    private final RadRepository radRepository;

    public List<MonitoringDto> pregled(Long ispitId, Long grupaId, StatusRada status) {
        return radRepository.monitoring(ispitId, grupaId, status);
    }
}