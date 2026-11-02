package org.example.razvojsoftvera.responses;

public record NastavnikResponse(
        Long id,
        String ime,
        String prezime,
        String zvanje,
        Long korisnikId
) {
}
