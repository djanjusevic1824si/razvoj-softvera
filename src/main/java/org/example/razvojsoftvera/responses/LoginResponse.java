package org.example.razvojsoftvera.responses;

import org.example.razvojsoftvera.model.Uloga;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        Long korisnikId,
        Uloga uloga
) {
}