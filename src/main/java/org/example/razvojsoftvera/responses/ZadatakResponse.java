package org.example.razvojsoftvera.responses;

import java.time.LocalDateTime;

public record ZadatakResponse(
        Long id,
        String naziv,
        String opis,
        String putanjaZadatka,
        LocalDateTime datumObjavljivanja,
        Long nastavnikId,
        String imeNastavnika,
        String prezimeNastavnika
) {}

