package org.example.razvojsoftvera.responses;

public record PrijavaResponse(
        Long id,
        Long ispitId,
        Long studentIndeksId,
        Long studentId,
        String ime,
        String prezime,
        Integer brojIndeksa,
        Integer godinaUpisa,
        Long studijskiProgramId
) {}
