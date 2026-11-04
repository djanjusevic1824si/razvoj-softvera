package org.example.razvojsoftvera.repositories;

import org.example.razvojsoftvera.model.StudentIndeks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentIndeksRepository
        extends JpaRepository<StudentIndeks, Long> {

    @Override
    @EntityGraph(attributePaths = {"student", "studijskiProgram"})
    List<StudentIndeks> findAll();

    @Override
    @EntityGraph(attributePaths = {"student", "studijskiProgram"})
    Page<StudentIndeks> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"student", "studijskiProgram"})
    Optional<StudentIndeks> findById(Long id);

    boolean existsByStudijskiProgram_IdAndBrojAndGodinaUpisa(
            Long studijskiProgramId,
            Integer broj,
            Integer godinaUpisa
    );

    boolean existsByStudijskiProgram_IdAndBrojAndGodinaUpisaAndIdNot(
            Long studijskiProgramId,
            Integer broj,
            Integer godinaUpisa,
            Long id
    );

    @EntityGraph(attributePaths = {"student", "studijskiProgram"})
    Page<StudentIndeks> findAllByStudent_Id(
            Long studentId,
            Pageable pageable
    );

    boolean existsByIdAndStudent_Korisnik_Id(
            Long indeksId,
            Long korisnikId
    );
}