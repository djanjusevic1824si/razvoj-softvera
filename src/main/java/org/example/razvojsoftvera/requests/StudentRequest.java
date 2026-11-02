package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.NotBlank;

public record StudentRequest(
        @NotBlank String ime,
        @NotBlank String prezime,
        String jmbg,
        Long korisnikId
) {
}
