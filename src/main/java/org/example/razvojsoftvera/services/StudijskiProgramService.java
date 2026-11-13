package org.example.razvojsoftvera.services;

import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.exceptions.ConflictException;
import org.example.razvojsoftvera.exceptions.ResourceNotFoundException;
import org.example.razvojsoftvera.model.StudijskiProgram;
import org.example.razvojsoftvera.repositories.StudijskiProgramRepository;
import org.example.razvojsoftvera.requests.StudijskiProgramRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@PreAuthorize("hasRole('NASTAVNIK')")
public class StudijskiProgramService {

    private final StudijskiProgramRepository studijskiProgramRepository;

    @Transactional
    public StudijskiProgram save(StudijskiProgramRequest request) {
        String oznaka = request.oznaka().trim().toUpperCase(Locale.ROOT);

        if (studijskiProgramRepository.existsByOznakaIgnoreCase(oznaka)) {
            throw new ConflictException(
                    "Studijski program sa tom oznakom vec postoji"
            );
        }

        StudijskiProgram studijskiProgram = new StudijskiProgram();
        studijskiProgram.setOznaka(oznaka);
        studijskiProgram.setNaziv(request.naziv().trim());

        try {
            return studijskiProgramRepository.saveAndFlush(studijskiProgram);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Studijski program nije moguce sacuvati zbog duplikata ili konflikta podataka"
            );
        }
    }

    @Transactional
    public StudijskiProgram update(
            Long id,
            StudijskiProgramRequest request
    ) {
        StudijskiProgram studijskiProgram = findByIdOrThrow(id);

        String oznaka = request.oznaka().trim().toUpperCase(Locale.ROOT);

        if (studijskiProgramRepository
                .existsByOznakaIgnoreCaseAndIdNot(oznaka, id)) {
            throw new ConflictException(
                    "Studijski program sa tom oznakom vec postoji"
            );
        }

        studijskiProgram.setOznaka(oznaka);
        studijskiProgram.setNaziv(request.naziv().trim());

        try {
            return studijskiProgramRepository.saveAndFlush(studijskiProgram);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Studijski program nije moguce izmeniti zbog duplikata ili konflikta podataka"
            );
        }
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('NASTAVNIK')")
    public List<StudijskiProgram> findAll() {
        return studijskiProgramRepository.findAll();
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('NASTAVNIK')")
    public Page<StudijskiProgram> findAll(Pageable pageable) {
        return studijskiProgramRepository.findAll(pageable);
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('NASTAVNIK')")
    public StudijskiProgram findById(Long id) {
        return findByIdOrThrow(id);
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('NASTAVNIK')")
    public List<String> findAllOznaka() {
        return studijskiProgramRepository.findAllOznaka();
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('NASTAVNIK')")
    public Page<StudijskiProgram> search(
            String oznaka,
            String naziv,
            Pageable pageable
    ) {
        String oznakaPretraga = oznaka == null ? "" : oznaka.trim();
        String nazivPretraga = naziv == null ? "" : naziv.trim();

        return studijskiProgramRepository
                .findByOznakaContainingIgnoreCaseAndNazivContainingIgnoreCase(
                        oznakaPretraga,
                        nazivPretraga,
                        pageable
                );
    }

    @Transactional
    public void deleteById(Long id) {
        StudijskiProgram studijskiProgram = findByIdOrThrow(id);

        try {
            studijskiProgramRepository.delete(studijskiProgram);
            studijskiProgramRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Studijski program ne moze da se obrise jer postoje povezani podaci"
            );
        }
    }

    private StudijskiProgram findByIdOrThrow(Long id) {
        return studijskiProgramRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Studijski program sa id " + id + " ne postoji"
                ));
    }
}