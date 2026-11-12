package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OcenaRequest(
        @NotNull Long nastavnikId,
        @NotNull @DecimalMin("0.0") @Digits(integer = 3, fraction = 1) BigDecimal poeni
) {}
