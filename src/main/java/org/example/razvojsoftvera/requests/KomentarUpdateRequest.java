package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record KomentarUpdateRequest(
        @NotBlank String putanjaFajla,
        @NotNull Integer linijaOd,
        @NotNull Integer linijaDo,
        @NotBlank String tekst
) {}