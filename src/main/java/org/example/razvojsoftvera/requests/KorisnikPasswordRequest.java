package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.NotBlank;

public record KorisnikPasswordRequest(
        @NotBlank String novaLozinka
) {
}