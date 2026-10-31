package org.example.razvojsoftvera.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_indeks_id", "ispit_id"})
})
@Data
public class Prijava {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private StudentIndeks studentIndeks;

    @ManyToOne(optional = false)
    private Ispit ispit;
}