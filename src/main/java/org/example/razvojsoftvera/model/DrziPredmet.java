package org.example.razvojsoftvera.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"nastavnik_id", "predmet_id", "studijski_program_id"})
)
@Data
public class DrziPredmet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Nastavnik nastavnik;

    @ManyToOne(optional = false)
    private Predmet predmet;

    @ManyToOne(optional = false)
    private StudijskiProgram studijskiProgram;
}
