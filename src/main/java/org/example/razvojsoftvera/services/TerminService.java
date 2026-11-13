package org.example.razvojsoftvera.services;

import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.exceptions.BadRequestException;
import org.example.razvojsoftvera.exceptions.ConflictException;
import org.example.razvojsoftvera.exceptions.ResourceNotFoundException;
import org.example.razvojsoftvera.model.Ispit;
import org.example.razvojsoftvera.model.Termin;
import org.example.razvojsoftvera.repositories.GrupaRepository;
import org.example.razvojsoftvera.repositories.IspitRepository;
import org.example.razvojsoftvera.repositories.TerminRepository;
import org.example.razvojsoftvera.requests.TerminCreateRequest;
import org.example.razvojsoftvera.requests.TerminUpdateRequest;
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
public class TerminService {

    private final TerminRepository repository;
    private final IspitRepository ispitRepository;
    private final GrupaRepository grupaRepository;

    @Transactional
    public Termin save(TerminCreateRequest request) {
        if (request.krajIzrade().isBefore(request.pocetakIzrade())) {
            throw new BadRequestException(
                    "Kraj izrade ne sme biti pre pocetka izrade"
            );
        }

        Ispit ispit = ispitRepository.findById(request.ispitId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ispit sa id " + request.ispitId() + " ne postoji"
                ));

        if (request.pocetakIzrade().toLocalDate()
                .isBefore(ispit.getIspitniRok().getDatumOd())
                || request.krajIzrade().toLocalDate()
                .isAfter(ispit.getIspitniRok().getDatumDo())) {
            throw new BadRequestException(
                    "Termin mora biti unutar ispitnog roka"
            );
        }

        Termin termin = new Termin();
        termin.setPocetakIzrade(request.pocetakIzrade());
        termin.setKrajIzrade(request.krajIzrade());
        termin.setIspit(ispit);

        try {
            return repository.saveAndFlush(termin);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Termin nije moguce sacuvati zbog konflikta sa podacima u bazi"
            );
        }
    }

    @Transactional
    public Termin update(Long id, TerminUpdateRequest request) {
        Termin termin = findByIdOrThrow(id);

        if (request.krajIzrade().isBefore(request.pocetakIzrade())) {
            throw new BadRequestException(
                    "Kraj izrade ne sme biti pre pocetka izrade"
            );
        }

        Ispit ispit = ispitRepository.findById(request.ispitId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ispit sa id " + request.ispitId() + " ne postoji"
                ));

        if (request.pocetakIzrade().toLocalDate()
                .isBefore(ispit.getIspitniRok().getDatumOd())
                || request.krajIzrade().toLocalDate()
                .isAfter(ispit.getIspitniRok().getDatumDo())) {
            throw new BadRequestException(
                    "Termin mora biti unutar ispitnog roka"
            );
        }

        boolean menjaIspit = !termin.getIspit().getId()
                .equals(ispit.getId());

        if (menjaIspit && grupaRepository.existsByTermin_Id(id)) {
            throw new ConflictException(
                    "Termin sa grupama ne moze da se prebaci na drugi ispit"
            );
        }

        termin.setPocetakIzrade(request.pocetakIzrade());
        termin.setKrajIzrade(request.krajIzrade());
        termin.setIspit(ispit);

        try {
            return repository.saveAndFlush(termin);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Termin nije moguce izmeniti zbog konflikta sa podacima u bazi"
            );
        }
    }


    @PreAuthorize("hasRole('STUDENT') or hasRole('NASTAVNIK')")
    public List<Termin> findAll() {
        return repository.findAll();
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('NASTAVNIK')")
    public Page<Termin> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('NASTAVNIK')")
    public Termin findById(Long id) {
        return findByIdOrThrow(id);
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('NASTAVNIK')")
    public Page<Termin> findByIspit(
            Long ispitId,
            Pageable pageable
    ) {
        if (!ispitRepository.existsById(ispitId)) {
            throw new ResourceNotFoundException(
                    "Ispit sa id " + ispitId + " ne postoji"
            );
        }

        return repository.findAllByIspit_Id(ispitId, pageable);
    }

    @Transactional
    public void deleteById(Long id) {
        Termin termin = findByIdOrThrow(id);

        try {
            repository.delete(termin);
            repository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Termin ne moze da se obrise jer postoje grupe koje mu pripadaju"
            );
        }
    }

    private Termin findByIdOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Termin sa id " + id + " ne postoji"
                ));
    }
}