package org.example.razvojsoftvera.services;

import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.exceptions.ConflictException;
import org.example.razvojsoftvera.exceptions.ResourceNotFoundException;
import org.example.razvojsoftvera.model.Grupa;
import org.example.razvojsoftvera.model.Termin;
import org.example.razvojsoftvera.model.Zadatak;
import org.example.razvojsoftvera.repositories.GrupaRepository;
import org.example.razvojsoftvera.repositories.RadRepository;
import org.example.razvojsoftvera.repositories.TerminRepository;
import org.example.razvojsoftvera.repositories.ZadatakRepository;
import org.example.razvojsoftvera.requests.GrupaCreateRequest;
import org.example.razvojsoftvera.requests.GrupaUpdateRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@PreAuthorize("hasRole('NASTAVNIK')")
public class GrupaService {

    private final GrupaRepository grupaRepository;
    private final TerminRepository terminRepository;
    private final ZadatakRepository zadatakRepository;
    private final RadRepository radRepository;

    @Transactional
    public Grupa save(GrupaCreateRequest request) {
        Termin termin = terminRepository.findById(request.terminId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Termin sa id " + request.terminId() + " ne postoji"
                ));

        Zadatak zadatak = zadatakRepository.findById(request.zadatakId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Zadatak sa id " + request.zadatakId() + " ne postoji"
                ));

        boolean postoji = grupaRepository.existsByTermin_IdAndNaziv(
                termin.getId(),
                request.naziv().trim()
        );

        if (postoji) {
            throw new ConflictException(
                    "Grupa sa tim nazivom vec postoji u terminu"
            );
        }

        Grupa grupa = new Grupa();
        grupa.setNaziv(request.naziv().trim());
        grupa.setTermin(termin);
        grupa.setZadatak(zadatak);

        try {
            return grupaRepository.saveAndFlush(grupa);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Grupu nije moguce sacuvati zbog duplikata ili konflikta povezanih podataka"
            );
        }
    }

    @Transactional
    public Grupa update(Long id, GrupaUpdateRequest request) {
        Grupa grupa = findByIdOrThrow(id);

        Termin termin = terminRepository.findById(request.terminId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Termin sa id " + request.terminId() + " ne postoji"
                ));

        Zadatak zadatak = zadatakRepository.findById(request.zadatakId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Zadatak sa id " + request.zadatakId() + " ne postoji"
                ));

        boolean postoji = grupaRepository.existsByTermin_IdAndNazivAndIdNot(
                termin.getId(),
                request.naziv().trim(),
                id
        );

        if (postoji) {
            throw new ConflictException(
                    "Grupa sa tim nazivom vec postoji u terminu"
            );
        }

        boolean menjaTermin = !grupa.getTermin().getId()
                .equals(termin.getId());

        boolean menjaZadatak = !grupa.getZadatak().getId()
                .equals(zadatak.getId());

        if ((menjaTermin || menjaZadatak)
                && radRepository.existsByGrupa_Id(id)) {
            throw new ConflictException(
                    "Grupi sa radovima ne moze da se promeni termin ili zadatak"
            );
        }

        grupa.setNaziv(request.naziv().trim());
        grupa.setTermin(termin);
        grupa.setZadatak(zadatak);

        try {
            return grupaRepository.saveAndFlush(grupa);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Grupu nije moguce izmeniti zbog duplikata ili konflikta povezanih podataka"
            );
        }
    }

    public List<Grupa> findAll() {
        return grupaRepository.findAll();
    }

    public Page<Grupa> findAll(Pageable pageable) {
        return grupaRepository.findAll(pageable);
    }

    public Grupa findById(Long id) {
        return findByIdOrThrow(id);
    }

    public Page<Grupa> findByTermin(
            Long terminId,
            Pageable pageable
    ) {
        if (!terminRepository.existsById(terminId)) {
            throw new ResourceNotFoundException(
                    "Termin sa id " + terminId + " ne postoji"
            );
        }

        return grupaRepository.findAllByTermin_Id(terminId, pageable);
    }

    @Transactional
    public void deleteById(Long id) {
        Grupa grupa = findByIdOrThrow(id);

        try {
            grupaRepository.delete(grupa);
            grupaRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Grupa ne moze da se obrise jer postoje radovi koji joj pripadaju"
            );
        }
    }

    private Grupa findByIdOrThrow(Long id) {
        return grupaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Grupa sa id " + id + " ne postoji"
                ));
    }
}