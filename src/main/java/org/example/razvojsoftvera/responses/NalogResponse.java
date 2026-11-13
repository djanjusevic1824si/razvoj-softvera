package org.example.razvojsoftvera.responses;

import org.example.razvojsoftvera.model.Uloga;

public record NalogResponse(
        Long korisnikId,
        Long profilId,
        String email,
        Uloga uloga
) {
}

