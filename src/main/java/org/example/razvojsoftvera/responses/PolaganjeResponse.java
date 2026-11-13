package org.example.razvojsoftvera.responses;

import org.example.razvojsoftvera.model.StatusRada;

import java.time.LocalDateTime;

public record PolaganjeResponse(
        Long radId,
        StatusRada statusRada,
        LocalDateTime vremePreuzimanja,
        String ucionica,
        Long grupaId,
        String nazivGrupe,
        Long terminId,
        LocalDateTime pocetakIzrade,
        LocalDateTime krajIzrade,
        String predmet,
        String nazivZadatka
) {
}
