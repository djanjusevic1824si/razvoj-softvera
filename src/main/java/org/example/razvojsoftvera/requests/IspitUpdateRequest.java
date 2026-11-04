package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.NotNull;

public record IspitUpdateRequest(
        @NotNull Long predmetId,
        @NotNull Long ispitniRokId
) {}