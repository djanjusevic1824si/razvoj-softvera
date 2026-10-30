package org.example.razvojsoftvera.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ime;

    @Column(nullable = false)
    private String prezime;

    @Column(unique = true)
    private String jmbg;

    @OneToOne
    @JoinColumn(unique = true)
    private Korisnik korisnik;
}
