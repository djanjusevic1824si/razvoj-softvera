package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record KomentarCreateRequest(
        @NotNull Long radId,
        @NotBlank String putanjaFajla,
        @NotNull Integer linijaOd,
        @NotNull Integer linijaDo,
        @NotBlank String tekst
) {}