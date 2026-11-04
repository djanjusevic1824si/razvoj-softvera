package org.example.razvojsoftvera.repositories;

import org.example.razvojsoftvera.model.StudijskiProgram;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudijskiProgramRepository
        extends JpaRepository<StudijskiProgram, Long> {

    boolean existsByOznakaIgnoreCase(String oznaka);

    boolean existsByOznakaIgnoreCaseAndIdNot(String oznaka, Long id);

    @Query("""
            SELECT s.oznaka
            FROM StudijskiProgram s
            ORDER BY s.oznaka
            """)
    List<String> findAllOznaka();

    Page<StudijskiProgram> findByOznakaContainingIgnoreCaseAndNazivContainingIgnoreCase(
            String oznaka,
            String naziv,
            Pageable pageable
    );
}