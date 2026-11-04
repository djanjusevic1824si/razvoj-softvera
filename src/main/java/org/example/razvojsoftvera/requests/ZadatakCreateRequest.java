package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ZadatakCreateRequest(
        @NotBlank String naziv,
        String opis,
        @NotBlank String putanjaZadatka,
        @NotNull Long nastavnikId
) {}
