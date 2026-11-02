package org.example.razvojsoftvera.repositories;

import org.example.razvojsoftvera.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByKorisnikId(Long korisnikId);

    boolean existsByJmbg(String jmbg);

    @Query("""
            select s from Student s
            where (:ime is null or lower(s.ime) like lower(concat('%', :ime, '%')))
              and (:prezime is null or lower(s.prezime) like lower(concat('%', :prezime, '%')))
            """)
    List<Student> findByImeAndPrezime(
            @Param("ime") String ime,
            @Param("prezime") String prezime
    );

}
