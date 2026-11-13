package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.NotNull;

public record PrijavaUpdateRequest(
        @NotNull Long studentIndeksId,
        @NotNull Long ispitId
) {}
