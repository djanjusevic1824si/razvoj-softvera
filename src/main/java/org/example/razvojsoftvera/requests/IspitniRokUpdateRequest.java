package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record IspitniRokUpdateRequest(
        @NotBlank String naziv,
        @NotBlank String skolskaGodina,
        @NotNull LocalDate datumOd,
        @NotNull LocalDate datumDo
) {}
