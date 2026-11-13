package org.example.razvojsoftvera.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ResenjeResponse(
        Long predajaId, Long radId,
        String studijskiProgram, Integer broj, Integer godinaUpisa,
        String ime, String prezime, String grupa,
        String putanjaResenja, LocalDateTime vremePredaje,
        BigDecimal poeni, LocalDateTime vremeOcenjivanja
){ }