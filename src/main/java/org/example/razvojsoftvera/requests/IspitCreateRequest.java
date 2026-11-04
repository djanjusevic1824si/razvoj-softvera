package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.NotNull;

public record IspitCreateRequest(
        @NotNull Long predmetId,
        @NotNull Long ispitniRokId
) {}