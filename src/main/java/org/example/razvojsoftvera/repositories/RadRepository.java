package org.example.razvojsoftvera.repositories;

import jakarta.persistence.LockModeType;
import org.example.razvojsoftvera.model.Rad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

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
}