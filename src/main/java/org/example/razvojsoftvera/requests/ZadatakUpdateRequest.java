package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.NotBlank;

public record ZadatakUpdateRequest(
        @NotBlank String naziv,
        String opis
) {}
