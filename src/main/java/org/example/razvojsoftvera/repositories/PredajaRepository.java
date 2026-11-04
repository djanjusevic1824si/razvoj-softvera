package org.example.razvojsoftvera.repositories;

import org.example.razvojsoftvera.model.Predaja;
import org.example.razvojsoftvera.model.TipPredaje;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PredajaRepository extends JpaRepository<Predaja, Long> {

    @Override
    @EntityGraph(attributePaths = "rad")
    List<Predaja> findAll();

    @Override
    @EntityGraph(attributePaths = "rad")
    Page<Predaja> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = "rad")
    Optional<Predaja> findById(Long id);

    boolean existsByRad_IdAndTipPredaje(
            Long radId,
            TipPredaje tipPredaje
    );

    @Query("""
            select coalesce(max(p.redniBroj), 0)
            from Predaja p
            where p.rad.id = :radId
            """)
    Integer findMaxRedniBrojByRadId(@Param("radId") Long radId);

    @EntityGraph(attributePaths = "rad")
    Page<Predaja> findAllByRad_Id(
            Long radId,
            Pageable pageable
    );
}