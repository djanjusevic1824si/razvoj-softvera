package org.example.razvojsoftvera.responses;

import org.example.razvojsoftvera.model.Uloga;

public record KorisnikResponse(
        Long id,
        String email,
        Uloga uloga
) {
}
