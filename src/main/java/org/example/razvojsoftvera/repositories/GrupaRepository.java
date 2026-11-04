package org.example.razvojsoftvera.repositories;

import org.example.razvojsoftvera.model.Grupa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GrupaRepository extends JpaRepository<Grupa, Long> {
    @Override
    @EntityGraph(attributePaths = {"termin", "zadatak"})
    List<Grupa> findAll();

    @Override
    @EntityGraph(attributePaths = {"termin", "zadatak"})
    Page<Grupa> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"termin", "zadatak"})
    Optional<Grupa> findById(Long id);

    boolean existsByTermin_Id(Long terminId);
    boolean existsByTermin_IdAndNaziv(Long terminId, String naziv);
    boolean existsByTermin_IdAndNazivAndIdNot(Long terminId, String naziv, Long id);

    @EntityGraph(attributePaths = {"termin", "zadatak"})
    Page<Grupa> findAllByTermin_Id(Long terminId, Pageable pageable);

}
