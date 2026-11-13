package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.NotNull;

public record DrziPredmetCreateRequest(
        @NotNull Long nastavnikId,
        @NotNull Long predmetId,
        @NotNull Long studijskiProgramId
) {}