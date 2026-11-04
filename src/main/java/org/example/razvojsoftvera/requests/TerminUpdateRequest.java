package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TerminUpdateRequest(
        @NotNull LocalDateTime pocetakIzrade,
        @NotNull LocalDateTime krajIzrade,
        @NotNull Long ispitId
) {}
