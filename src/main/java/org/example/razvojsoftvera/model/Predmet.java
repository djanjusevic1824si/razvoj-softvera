package org.example.razvojsoftvera.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"studijski_program_id", "sifra"})
)
@Data
public class Predmet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sifra;

    @Column(nullable = false)
    private String naziv;

    @ManyToOne(optional = false)
    private StudijskiProgram studijskiProgram;
}
