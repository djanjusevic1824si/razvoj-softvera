package org.example.razvojsoftvera.repositories;

import org.example.razvojsoftvera.model.Predmet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PredmetRepository extends JpaRepository<Predmet, Long> {

    boolean existsBySifraIgnoreCase(String sifra);

    boolean existsBySifraIgnoreCaseAndIdNot(String sifra, Long id);

    List<Predmet> findBySifraContainingIgnoreCase(String sifra);

    List<Predmet> findByNazivContainingIgnoreCase(String naziv);

    Page<Predmet> findBySifraContainingIgnoreCaseAndNazivContainingIgnoreCase(
            String sifra,
            String naziv,
            Pageable pageable
    );
}