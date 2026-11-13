package org.example.razvojsoftvera.responses;

import org.example.razvojsoftvera.model.StatusRada;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RadResponse(
        Long id,
        Long prijavaId,
        Long grupaId,
        String ucionica,
        StatusRada statusRada,
        LocalDateTime vremePreuzimanja,
        BigDecimal poeni,
        Long ocenioNastavnikId,
        LocalDateTime vremeOcenjivanja
) {}