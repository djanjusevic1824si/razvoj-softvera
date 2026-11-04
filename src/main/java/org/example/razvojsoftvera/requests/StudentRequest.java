package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record StudentRequest(
        @NotBlank String ime,
        @NotBlank String prezime,
        String jmbg,
        @NotNull @Positive Long korisnikId
) {
}