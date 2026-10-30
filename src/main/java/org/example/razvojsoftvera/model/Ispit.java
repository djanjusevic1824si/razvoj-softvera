package org.example.razvojsoftvera.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"ispitni_rok_id", "predmet_id"})
)
@Data
public class Ispit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Predmet predmet;

    @ManyToOne(optional = false)
    private IspitniRok ispitniRok;
}
