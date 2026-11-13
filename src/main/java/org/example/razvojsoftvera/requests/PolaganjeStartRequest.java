package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PolaganjeStartRequest(
        @NotNull Long terminId,
        @NotNull Long grupaId,
        @Size(max = 50)String ucionica
) {
}
