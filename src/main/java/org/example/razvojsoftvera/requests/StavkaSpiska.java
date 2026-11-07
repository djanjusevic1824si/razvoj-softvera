package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StavkaSpiska(
        @NotBlank String program,
        @NotNull Integer broj,
        @NotNull Integer godinaUpisa
) {
}
