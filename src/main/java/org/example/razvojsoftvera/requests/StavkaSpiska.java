package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record StavkaSpiska(
        @NotBlank String program,
        @NotNull @Positive Integer broj,
        @NotNull @Min(2000) Integer godinaUpisa
) {
}
