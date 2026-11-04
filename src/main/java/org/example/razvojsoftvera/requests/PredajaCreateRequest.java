package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.razvojsoftvera.model.TipPredaje;

public record PredajaCreateRequest(
        @NotNull Long radId,
        @NotNull TipPredaje tipPredaje,
        @NotBlank String putanjaResenja
) {}