package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record NastavnikRequest(
        @NotBlank String ime,
        @NotBlank String prezime,
        String zvanje,
        @NotNull @Positive Long korisnikId
) {
}