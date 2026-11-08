package org.example.razvojsoftvera.repositories;

import jakarta.persistence.LockModeType;
import org.example.razvojsoftvera.model.Rad;
import org.example.razvojsoftvera.model.StatusRada;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RadRepository extends JpaRepository<Rad, Long> {

    @Override
    @EntityGraph(attributePaths = {"prijava", "grupa", "ocenioNastavnik"})
    List<Rad> findAll();

    @Override
    @EntityGraph(attributePaths = {"prijava", "grupa", "ocenioNastavnik"})
    Page<Rad> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"prijava", "grupa", "ocenioNastavnik"})
    Optional<Rad> findById(Long id);

    boolean existsByPrijava_Id(Long prijavaId);

    boolean existsByGrupa_Id(Long grupaId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Rad r where r.id = :id")
    Optional<Rad> findByIdForUpdate(@Param("id") Long id);

    @EntityGraph(attributePaths = {"prijava", "grupa", "ocenioNastavnik"})
    Page<Rad> findAllByPrijava_Ispit_Id(
            Long ispitId,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"prijava", "grupa.termin.ispit.predmet", "grupa.zadatak"})
    Optional<Rad> findByPrijava_Id(Long prijavaId);

    @Query("""
        select r from Rad r
        where r.statusRada = :status
          and r.grupa.termin.krajIzrade < :granica
        """)
    List<Rad> nadjiIstekleRadove(StatusRada status, LocalDateTime granica);
}