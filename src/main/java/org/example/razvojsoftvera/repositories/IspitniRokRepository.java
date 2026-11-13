package org.example.razvojsoftvera.repositories;

import org.example.razvojsoftvera.model.IspitniRok;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface IspitniRokRepository extends JpaRepository<IspitniRok, Long> {

    boolean existsByNazivAndSkolskaGodina(String naziv, String skolskaGodina);
    boolean existsByNazivAndSkolskaGodinaAndIdNot(String naziv, String skolskaGodina, Long id);
    Page<IspitniRok> findAllBySkolskaGodina(String skolskaGodina, Pageable pageable);
    Page<IspitniRok> findAllByNazivContainingIgnoreCase(String naziv, Pageable pageable);
    List<IspitniRok> findAllByDatumOdLessThanEqualAndDatumDoGreaterThanEqualOrderByDatumOdAsc(LocalDate datum1, LocalDate datum2);

}
