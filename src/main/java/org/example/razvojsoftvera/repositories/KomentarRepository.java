package org.example.razvojsoftvera.repositories;

import org.example.razvojsoftvera.model.Komentar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface KomentarRepository extends JpaRepository<Komentar, Long> {

    @Override
    @EntityGraph(attributePaths = {"rad", "nastavnik"})
    List<Komentar> findAll();

    @Override
    @EntityGraph(attributePaths = {"rad", "nastavnik"})
    Page<Komentar> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"rad", "nastavnik"})
    Optional<Komentar> findById(Long id);

    @EntityGraph(attributePaths = {"rad", "nastavnik"})
    Page<Komentar> findAllByRad_Id(Long radId, Pageable pageable);

    @EntityGraph(attributePaths = {"rad", "nastavnik"})
    Page<Komentar> findAllByRad_Prijava_StudentIndeks_Student_Korisnik_Id(
            Long korisnikId, Pageable pageable);
}