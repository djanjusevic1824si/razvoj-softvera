package org.example.razvojsoftvera.responses;

public record DrziPredmetResponse(
        Long id,
        Long nastavnikId,
        Long predmetId,
        Long studijskiProgramId
) {}