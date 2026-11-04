package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ZadatakUpdateRequest(
        @NotBlank String naziv,
        String opis,
        @NotBlank String putanjaZadatka,
        @NotNull Long nastavnikId
) {}
