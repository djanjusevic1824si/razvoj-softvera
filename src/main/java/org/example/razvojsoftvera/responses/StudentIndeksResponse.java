package org.example.razvojsoftvera.responses;

public record StudentIndeksResponse(
        Long id,
        Integer broj,
        Integer godinaUpisa,
        boolean aktivan,
        Long studentId,
        Long studijskiProgramId
) {}