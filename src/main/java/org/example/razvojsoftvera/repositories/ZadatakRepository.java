package org.example.razvojsoftvera.repositories;

import org.example.razvojsoftvera.model.Zadatak;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZadatakRepository extends JpaRepository<Zadatak, Long> {
}
