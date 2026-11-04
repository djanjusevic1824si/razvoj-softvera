package org.example.razvojsoftvera.responses;

import org.example.razvojsoftvera.model.TipPredaje;

import java.time.LocalDateTime;

public record PredajaResponse(
        Long id,
        Long radId,
        Integer redniBroj,
        TipPredaje tipPredaje,
        LocalDateTime vreme,
        String putanjaResenja
) {}