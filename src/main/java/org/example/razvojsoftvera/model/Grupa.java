package org.example.razvojsoftvera.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"termin_id", "naziv"})
)
@Data
public class Grupa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String naziv;

    @ManyToOne(optional = false)
    private Termin termin;

    @ManyToOne(optional = false)
    private Zadatak zadatak;
}
