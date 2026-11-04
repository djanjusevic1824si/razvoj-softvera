package org.example.razvojsoftvera.repositories;

import org.example.razvojsoftvera.model.Termin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TerminRepository extends JpaRepository<Termin, Long> {
    @Override
    @EntityGraph(attributePaths = {"ispit"})
    List<Termin> findAll();

    @Override
    @EntityGraph(attributePaths = {"ispit"})
    Page<Termin> findAll(Pageable pageable);

    boolean existsByIspit_Id(Long ispitId);

    @Override
    @EntityGraph(attributePaths = {"ispit"})
    Optional<Termin> findById(Long id);

    @EntityGraph(attributePaths = {"ispit"})
    Page<Termin> findAllByIspit_Id(Long ispitId, Pageable pageable);

    @Query("select (count(t) > 0) from Termin t where t.ispit.ispitniRok.id = :rokId "
            + "and (t.pocetakIzrade < :od or t.krajIzrade >= :doIskljucivo)")
    boolean postojiVanRoka(@Param("rokId") Long rokId, @Param("od") LocalDateTime od,
                           @Param("doIskljucivo") LocalDateTime doIskljucivo);

}
