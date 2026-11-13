package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RadOcenaRequest(
        @NotNull BigDecimal poeni,
        @NotNull Long nastavnikId
) {}