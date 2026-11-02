package org.example.razvojsoftvera.repositories;

import org.example.razvojsoftvera.model.Nastavnik;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NastavnikRepository extends JpaRepository<Nastavnik, Long> {
    boolean existsByKorisnikId(Long korisnikId);
    @Query("""
            select n from Nastavnik n
            where (:ime is null or lower(n.ime) like lower(concat('%', :ime, '%')))
              and (:prezime is null or lower(n.prezime) like lower(concat('%', :prezime, '%')))
            """)
    List<Nastavnik> findByImeAndPrezime(
            @Param("ime") String ime,
            @Param("prezime") String prezime
    );
}
