package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.NotNull;

public record StudentIndeksCreateRequest(
        @NotNull Integer broj,
        @NotNull Integer godinaUpisa,
        @NotNull Long studentId,
        @NotNull Long studijskiProgramId
) {}