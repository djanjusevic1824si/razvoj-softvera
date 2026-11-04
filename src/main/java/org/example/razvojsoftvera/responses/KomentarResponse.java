package org.example.razvojsoftvera.responses;

import java.time.LocalDateTime;

public record KomentarResponse(
        Long id,
        Long radId,
        Long nastavnikId,
        String putanjaFajla,
        Integer linijaOd,
        Integer linijaDo,
        String tekst,
        LocalDateTime vremePostavljanja
) {}