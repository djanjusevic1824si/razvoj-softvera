package org.example.razvojsoftvera.model;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(
        uniqueConstraints = {@UniqueConstraint(columnNames = {"rad_id", "redni_broj"})},
        check = {
                @CheckConstraint(name = "ck_predaja_tip", constraint = "tip_predaje in ('AUTOMATSKA', 'KONACNA')"),
                @CheckConstraint(name = "ck_predaja_redni_broj", constraint = "redni_broj >= 1")
        }
)
@Data
public class Predaja { // TODO: NAJVISE 1 KONACNA PREDAJA!!!!

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer redniBroj;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipPredaje tipPredaje = TipPredaje.AUTOMATSKA;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime vreme;

    @Column(nullable = false)
    private String putanjaResenja;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(optional = false)
    private Rad rad;
}
