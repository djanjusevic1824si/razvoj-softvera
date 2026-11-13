package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record TerminCreateRequest(
        @NotNull LocalDateTime pocetakIzrade,
        @NotNull LocalDateTime krajIzrade,
        @NotNull Long ispitId
) {}
