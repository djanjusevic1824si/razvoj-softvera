package org.example.razvojsoftvera.repositories;

import org.example.razvojsoftvera.model.Nastavnik;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NastavnikRepository extends JpaRepository<Nastavnik, Long> {

    @Override
    @EntityGraph(attributePaths = "korisnik")
    List<Nastavnik> findAll();

    @Override
    @EntityGraph(attributePaths = "korisnik")
    Page<Nastavnik> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = "korisnik")
    Optional<Nastavnik> findById(Long id);

    boolean existsByKorisnikId(Long korisnikId);

    @EntityGraph(attributePaths = "korisnik")
    Optional<Nastavnik> findByKorisnikId(Long korisnikId);

    @EntityGraph(attributePaths = "korisnik")
    List<Nastavnik> findByImeContainingIgnoreCaseAndPrezimeContainingIgnoreCase(
            String ime,
            String prezime
    );

    @EntityGraph(attributePaths = "korisnik")
    Page<Nastavnik> findByImeContainingIgnoreCaseAndPrezimeContainingIgnoreCase(
            String ime,
            String prezime,
            Pageable pageable
    );

    @Query("select n.id from Nastavnik n where n.korisnik.id = :korisnikId")
    Optional<Long> findIdByKorisnikId(@Param("korisnikId")Long korisnikid);
}