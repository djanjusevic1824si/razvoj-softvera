package org.example.razvojsoftvera.repositories;

import org.example.razvojsoftvera.model.Zadatak;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ZadatakRepository extends JpaRepository<Zadatak, Long> {
    @Override
    @EntityGraph(attributePaths = {"nastavnik"})
    List<Zadatak> findAll();

    @Override
    @EntityGraph(attributePaths = {"nastavnik"})
    Page<Zadatak> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"nastavnik"})
    Optional<Zadatak> findById(Long id);

    @EntityGraph(attributePaths = {"nastavnik"})
    Page<Zadatak> findAllByNastavnik_Id(Long nastavnikId, Pageable pageable);

    @EntityGraph(attributePaths = {"nastavnik"})
    Page<Zadatak> findAllByNazivContainingIgnoreCase(String naziv, Pageable pageable);

}
