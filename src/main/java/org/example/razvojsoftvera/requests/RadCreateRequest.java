package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.NotNull;

public record RadCreateRequest(
        @NotNull Long prijavaId,
        @NotNull Long grupaId,
        String ucionica
) {}