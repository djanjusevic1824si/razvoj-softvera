package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.NotBlank;

public record NastavnikUpdateRequest(
        @NotBlank String ime,
        @NotBlank String prezime,
        String zvanje
) {
}