package org.example.razvojsoftvera.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table (
        check = {
                @CheckConstraint(name = "ck_rad_status", constraint = "status_rada in ('PREUZET', 'PREDAT')"),
                @CheckConstraint(name = "ck_rad_poeni", constraint = "(ocenio_nastavnik_id is null and poeni is null and vreme_ocenjivanja is null)"
                + "or (ocenio_nastavnik_id is not null and poeni is not null and vreme_ocenjivanja is not null and poeni >= 0)")
        }
)
@Data
public class Rad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ucionica;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusRada statusRada = StatusRada.PREUZET;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime vremePreuzimanja;

    @Column(precision = 4, scale = 1)
    private BigDecimal poeni;

    private LocalDateTime vremeOcenjivanja;

    @OneToOne
    @JoinColumn(unique = true, nullable = false)
    private Prijava prijava;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Grupa grupa;

    @ManyToOne
    private Nastavnik ocenioNastavnik;
}
