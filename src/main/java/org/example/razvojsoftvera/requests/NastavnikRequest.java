package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NastavnikRequest(
        @NotBlank String ime,
        @NotBlank String prezime,
        String zvanje,
        @NotNull Long korisnikId
) {
}
