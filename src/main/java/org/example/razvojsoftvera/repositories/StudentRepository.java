package org.example.razvojsoftvera.repositories;

import org.example.razvojsoftvera.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @Override
    @EntityGraph(attributePaths = "korisnik")
    List<Student> findAll();

    @Override
    @EntityGraph(attributePaths = "korisnik")
    Page<Student> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = "korisnik")
    Optional<Student> findById(Long id);

    boolean existsByJmbg(String jmbg);

    boolean existsByJmbgAndIdNot(String jmbg, Long id);

    boolean existsByKorisnikId(Long korisnikId);

    boolean existsByIdAndKorisnik_Id(Long id, Long korisnikId);

    @EntityGraph(attributePaths = "korisnik")
    Optional<Student> findByKorisnikId(Long korisnikId);

    @EntityGraph(attributePaths = "korisnik")
    List<Student> findByImeContainingIgnoreCaseAndPrezimeContainingIgnoreCase(
            String ime,
            String prezime
    );

    @EntityGraph(attributePaths = "korisnik")
    Page<Student> findByImeContainingIgnoreCaseAndPrezimeContainingIgnoreCase(
            String ime,
            String prezime,
            Pageable pageable
    );

    @Query("select s.id from Student s where s.korisnik.id = :korisnikId")
    Optional<Long> findIdByKorisnikId(@Param("korisnikId") Long korisnikId);
}