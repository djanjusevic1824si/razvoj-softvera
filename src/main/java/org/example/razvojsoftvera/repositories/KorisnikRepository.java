package org.example.razvojsoftvera.repositories;

import org.example.razvojsoftvera.model.Korisnik;
import org.example.razvojsoftvera.model.Uloga;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KorisnikRepository extends JpaRepository<Korisnik, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    Optional<Korisnik> findByEmailIgnoreCase(String email);

    Page<Korisnik> findByUloga(Uloga uloga, Pageable pageable);
}