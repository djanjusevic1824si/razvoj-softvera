package org.example.razvojsoftvera.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Nastavnik {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ime;

    @Column(nullable = false)
    private String prezime;

    private String zvanje;

    @OneToOne(optional = false)
    @JoinColumn(unique = true)
    private Korisnik korisnik;
}
