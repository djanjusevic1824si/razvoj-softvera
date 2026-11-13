package org.example.razvojsoftvera.controllers;

import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.model.StatusRada;
import org.example.razvojsoftvera.responses.MonitoringDto;
import org.example.razvojsoftvera.services.MonitoringService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/monitoring")
@RequiredArgsConstructor
public class MonitoringController {
    private final MonitoringService service;

    @GetMapping
    public List<MonitoringDto> pregled(
            @RequestParam(required = false) Long ispitId,
            @RequestParam(required = false) Long grupaId,
            @RequestParam(required = false) StatusRada status) {
        return service.pregled(ispitId, grupaId, status);
    }
}