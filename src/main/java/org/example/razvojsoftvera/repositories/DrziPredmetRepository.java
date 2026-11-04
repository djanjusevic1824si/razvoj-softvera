package org.example.razvojsoftvera.repositories;

import org.example.razvojsoftvera.model.DrziPredmet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DrziPredmetRepository
        extends JpaRepository<DrziPredmet, Long> {

    @Override
    @EntityGraph(attributePaths = {"nastavnik", "predmet", "studijskiProgram"})
    List<DrziPredmet> findAll();

    @Override
    @EntityGraph(attributePaths = {"nastavnik", "predmet", "studijskiProgram"})
    Page<DrziPredmet> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"nastavnik", "predmet", "studijskiProgram"})
    Optional<DrziPredmet> findById(Long id);

    boolean existsByNastavnik_IdAndPredmet_IdAndStudijskiProgram_Id(
            Long nastavnikId,
            Long predmetId,
            Long studijskiProgramId
    );

    boolean existsByNastavnik_IdAndPredmet_IdAndStudijskiProgram_IdAndIdNot(
            Long nastavnikId,
            Long predmetId,
            Long studijskiProgramId,
            Long id
    );

    @EntityGraph(attributePaths = {"nastavnik", "predmet", "studijskiProgram"})
    Page<DrziPredmet> findAllByNastavnik_Id(
            Long nastavnikId,
            Pageable pageable
    );
}