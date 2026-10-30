package org.example.razvojsoftvera.model;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(
        check = {
                @CheckConstraint(name = "ck_komentar_broj_linija", constraint = "linija_od >= 1 and linija_do >= linija_od")
        }
)
@Data
public class Komentar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String putanjaFajla;

    @Column(nullable = false)
    private Integer linijaOd;

    @Column(nullable = false)
    private Integer linijaDo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String tekst;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime vremePostavljanja;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(optional = false)
    private Rad rad;

    @ManyToOne(optional = false)
    private Nastavnik nastavnik;
}
