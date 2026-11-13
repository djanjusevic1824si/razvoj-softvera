package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.NotNull;

public record PrijavaCreateRequest(
        @NotNull Long studentIndeksId,
        @NotNull Long ispitId
) {}
