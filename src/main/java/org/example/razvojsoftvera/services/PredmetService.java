package org.example.razvojsoftvera.services;

import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.exceptions.ConflictException;
import org.example.razvojsoftvera.exceptions.ResourceNotFoundException;
import org.example.razvojsoftvera.model.Predmet;
import org.example.razvojsoftvera.repositories.PredmetRepository;
import org.example.razvojsoftvera.requests.PredmetRequest;
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
public class PredmetService {

    private final PredmetRepository predmetRepository;

    @Transactional
    public Predmet save(PredmetRequest request) {
        String sifra = request.sifra().trim().toUpperCase(Locale.ROOT);

        if (predmetRepository.existsBySifraIgnoreCase(sifra)) {
            throw new ConflictException(
                    "Predmet sa tom sifrom vec postoji"
            );
        }

        Predmet predmet = new Predmet();
        predmet.setSifra(sifra);
        predmet.setNaziv(request.naziv().trim());

        try {
            return predmetRepository.saveAndFlush(predmet);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Predmet nije moguce sacuvati zbog duplikata ili konflikta podataka"
            );
        }
    }

    @Transactional
    public Predmet update(Long id, PredmetRequest request) {
        Predmet predmet = findByIdOrThrow(id);

        String sifra = request.sifra().trim().toUpperCase(Locale.ROOT);

        if (predmetRepository.existsBySifraIgnoreCaseAndIdNot(sifra, id)) {
            throw new ConflictException(
                    "Predmet sa tom sifrom vec postoji"
            );
        }

        predmet.setSifra(sifra);
        predmet.setNaziv(request.naziv().trim());

        try {
            return predmetRepository.saveAndFlush(predmet);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Predmet nije moguce izmeniti zbog duplikata ili konflikta podataka"
            );
        }
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('NASTAVNIK')")
    public List<Predmet> findAll() {
        return predmetRepository.findAll();
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('NASTAVNIK')")
    public Page<Predmet> findAll(Pageable pageable) {
        return predmetRepository.findAll(pageable);
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('NASTAVNIK')")
    public Predmet findById(Long id) {
        return findByIdOrThrow(id);
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('NASTAVNIK')")
    public List<Predmet> findBySifra(String sifra) {
        return predmetRepository.findBySifraContainingIgnoreCase(
                sifra.trim()
        );
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('NASTAVNIK')")
    public List<Predmet> findByNaziv(String naziv) {
        return predmetRepository.findByNazivContainingIgnoreCase(
                naziv.trim()
        );
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('NASTAVNIK')")
    public Page<Predmet> search(
            String sifra,
            String naziv,
            Pageable pageable
    ) {
        String sifraPretraga = sifra == null ? "" : sifra.trim();
        String nazivPretraga = naziv == null ? "" : naziv.trim();

        return predmetRepository
                .findBySifraContainingIgnoreCaseAndNazivContainingIgnoreCase(
                        sifraPretraga,
                        nazivPretraga,
                        pageable
                );
    }

    @Transactional
    public void deleteById(Long id) {
        Predmet predmet = findByIdOrThrow(id);

        try {
            predmetRepository.delete(predmet);
            predmetRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Predmet ne moze da se obrise jer postoje povezani podaci"
            );
        }
    }

    private Predmet findByIdOrThrow(Long id) {
        return predmetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Predmet sa id " + id + " ne postoji"
                ));
    }
}