package org.example.razvojsoftvera.model;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
public class Zadatak {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String naziv;

    private String opis;

    @Column(nullable = false)
    private String putanjaZadatka;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime datumObjavljivanja;

    @ManyToOne(optional = false)
    private Nastavnik nastavnik;
}
