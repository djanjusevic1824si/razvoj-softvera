package org.example.razvojsoftvera.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record KorisnikUpdateRequest(
        @NotBlank @Email String email
) {
}