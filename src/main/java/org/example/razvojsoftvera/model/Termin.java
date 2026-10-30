package org.example.razvojsoftvera.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(
        check = @CheckConstraint(name = "ck_termin_vreme_izrade", constraint = "kraj_izrade >= pocetak_izrade")
)
@Data
public class Termin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime pocetakIzrade;

    @Column(nullable = false)
    private LocalDateTime krajIzrade;

    @ManyToOne(optional = false)
    private Ispit ispit;
}
