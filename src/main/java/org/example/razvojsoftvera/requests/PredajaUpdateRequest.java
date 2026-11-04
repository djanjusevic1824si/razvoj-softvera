package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.NotBlank;

public record PredajaUpdateRequest(
        @NotBlank String putanjaResenja
) {}