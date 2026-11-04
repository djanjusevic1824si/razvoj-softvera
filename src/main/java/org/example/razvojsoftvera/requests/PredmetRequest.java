package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.NotBlank;

public record PredmetRequest(
        @NotBlank String sifra,
        @NotBlank String naziv
) {
}