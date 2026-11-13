package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GrupaUpdateRequest(
        @NotBlank String naziv,
        @NotNull Long terminId,
        @NotNull Long zadatakId
) {}
