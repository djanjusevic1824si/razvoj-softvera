package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.NotBlank;

public record StudijskiProgramRequest(
        @NotBlank String oznaka,
        @NotBlank String naziv
) {
}