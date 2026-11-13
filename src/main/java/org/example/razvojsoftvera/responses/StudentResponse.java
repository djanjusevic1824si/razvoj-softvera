package org.example.razvojsoftvera.responses;

public record StudentResponse(
        Long id,
        String ime,
        String prezime,
        String jmbg,
        Long korisnikId
) {
}
