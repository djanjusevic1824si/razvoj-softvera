package org.example.razvojsoftvera.responses;

import java.time.LocalDateTime;

public record TerminResponse(
        Long id,
        LocalDateTime pocetakIzrade,
        LocalDateTime krajIzrade,
        IspitResponse ispit
) { }
