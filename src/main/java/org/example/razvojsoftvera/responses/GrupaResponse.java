package org.example.razvojsoftvera.responses;

public record GrupaResponse(
        Long id,
        String naziv,
        Long terminId,
        Long zadatakId,
        String nazivZadatka
) {}
