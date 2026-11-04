package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.NotBlank;

public record StudentUpdateRequest(
        @NotBlank String ime,
        @NotBlank String prezime,
        String jmbg
) {
}