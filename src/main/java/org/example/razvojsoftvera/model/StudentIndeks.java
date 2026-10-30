package org.example.razvojsoftvera.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"studijski_program_id", "broj", "godina_upisa"})
)
@Data
public class StudentIndeks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer broj;

    @Column(nullable = false)
    private Integer godinaUpisa;

    @Column(nullable = false)
    private boolean aktivan = true;

    @ManyToOne(optional = false)
    private Student student;

    @ManyToOne(optional = false)
    private StudijskiProgram studijskiProgram;
}
