package org.example.razvojsoftvera.responses;

import org.example.razvojsoftvera.model.StatusRada;

import java.time.LocalDateTime;

public record MonitoringDto(
        Long studentIndeksId,
        String studijskiProgram,
        Integer brojIndeksa,
        Integer godinaUpisa,
        String ime,
        String prezime,
        Long radId,
        String grupa,
        String ucionica,
        StatusRada status,
        LocalDateTime vremePreuzimanja,
        LocalDateTime vremeKonacnePredaje,
        Long brojPredaja
) { }