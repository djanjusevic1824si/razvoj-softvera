package org.example.razvojsoftvera.repositories;

import org.example.razvojsoftvera.model.Komentar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KomentarRepository extends JpaRepository<Komentar, Long> {
}
