package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.razvojsoftvera.model.Uloga;

public record KorisnikRequest(
        @NotBlank @Email String email,
        @NotBlank String lozinka,
        @NotNull Uloga uloga
) {
}
