package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.NotNull;

public record StudentIndeksUpdateRequest(
        @NotNull Integer broj,
        @NotNull Integer godinaUpisa,
        @NotNull Boolean aktivan,
        @NotNull Long studentId,
        @NotNull Long studijskiProgramId
) {}