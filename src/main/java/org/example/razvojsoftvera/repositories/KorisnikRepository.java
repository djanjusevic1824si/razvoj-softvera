package org.example.razvojsoftvera.repositories;

import org.example.razvojsoftvera.model.Korisnik;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface KorisnikRepository extends JpaRepository<Korisnik, Long> {
    boolean existsByEmailIgnoreCase(String email);

    Optional<Korisnik> findByEmailIgnoreCase(String email);
}
