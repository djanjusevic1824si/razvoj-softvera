package org.example.razvojsoftvera.responses;

import java.time.LocalDate;

public record IspitniRokResponse(
        Long id,
        String naziv,
        String skolskaGodina,
        LocalDate datumOd,
        LocalDate datumDo
) { }
