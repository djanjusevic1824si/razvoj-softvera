package org.example.razvojsoftvera.repositories;

import org.example.razvojsoftvera.model.Ispit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IspitRepository extends JpaRepository<Ispit, Long> {

    @Override
    @EntityGraph(attributePaths = {"predmet", "ispitniRok"})
    List<Ispit> findAll();

    @Override
    @EntityGraph(attributePaths = {"predmet", "ispitniRok"})
    Page<Ispit> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"predmet", "ispitniRok"})
    Optional<Ispit> findById(Long id);

    boolean existsByPredmet_IdAndIspitniRok_Id(
            Long predmetId,
            Long ispitniRokId
    );

    boolean existsByPredmet_IdAndIspitniRok_IdAndIdNot(
            Long predmetId,
            Long ispitniRokId,
            Long id
    );

    @EntityGraph(attributePaths = {"predmet", "ispitniRok"})
    Page<Ispit> findAllByIspitniRok_Id(
            Long ispitniRokId,
            Pageable pageable
    );
}