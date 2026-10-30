package org.example.razvojsoftvera.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"naziv", "skolska_godina"}),
        check = @CheckConstraint(name = "ck_ispitni_rok_datumi", constraint = "datum_do >= datum_od")
)
@Data
public class IspitniRok {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String naziv;

    @Column(nullable = false)
    private String skolskaGodina;

    @Column(nullable = false)
    private LocalDate datumOd;

    @Column(nullable = false)
    private LocalDate datumDo;
}
