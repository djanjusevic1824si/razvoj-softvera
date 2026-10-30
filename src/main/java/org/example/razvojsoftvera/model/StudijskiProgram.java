package org.example.razvojsoftvera.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class StudijskiProgram {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false, unique = true)
    private String oznaka;

    @Column (nullable = false)
    private String naziv;
}
