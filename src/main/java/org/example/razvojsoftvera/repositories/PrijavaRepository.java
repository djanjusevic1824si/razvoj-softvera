package org.example.razvojsoftvera.repositories;

import org.example.razvojsoftvera.model.Prijava;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PrijavaRepository extends JpaRepository<Prijava, Long> {
    @Override
    @EntityGraph(attributePaths = {"ispit", "studentIndeks.student", "studentIndeks.studijskiProgram"})
    List<Prijava> findAll();

    @Override
    @EntityGraph(attributePaths = {"ispit", "studentIndeks.student", "studentIndeks.studijskiProgram"})
    Page<Prijava> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"ispit", "studentIndeks.student", "studentIndeks.studijskiProgram"})
    Optional<Prijava> findById(Long id);
    boolean existsByIspit_Id(Long ispitId);

    @EntityGraph(attributePaths = {"ispit", "studentIndeks.student", "studentIndeks.studijskiProgram"})
    Page<Prijava> findAllByIspit_Id(Long ispitId, Pageable pageable);

    @EntityGraph(attributePaths = {"ispit", "studentIndeks.student", "studentIndeks.studijskiProgram"})
    Page<Prijava> findAllByStudentIndeks_Id(Long studentIndeksId, Pageable pageable);

    @EntityGraph(attributePaths = {
            "ispit",
            "studentIndeks.student",
            "studentIndeks.studijskiProgram"
    })
    boolean existsByIdAndStudentIndeks_Student_Korisnik_Id(
            Long prijavaId,
            Long korisnikId
    );
    boolean existsByStudentIndeks_Id(Long studentIndeksId);
    boolean existsByStudentIndeks_IdAndIspit_Id(Long studentIndeksId, Long ispitId);
    boolean existsByStudentIndeks_IdAndIspit_IdAndIdNot(Long studentIndeksId, Long ispitId, Long id);
    long countByIspit_Id(Long ispitId);

    Optional<Prijava> findByStudentIndeks_IdAndIspit_Id(Long studentIndeksId, Long ispitId);

}
