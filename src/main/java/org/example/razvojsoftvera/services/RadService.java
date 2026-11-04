package org.example.razvojsoftvera.services;

import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.exceptions.*;
import org.example.razvojsoftvera.model.*;
import org.example.razvojsoftvera.repositories.*;
import org.example.razvojsoftvera.requests.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RadService {

    private final RadRepository radRepository;
    private final PrijavaRepository prijavaRepository;
    private final GrupaRepository grupaRepository;
    private final NastavnikRepository nastavnikRepository;
    private final IspitRepository ispitRepository;

    @Transactional
    public Rad save(RadCreateRequest request) {
        Prijava prijava = prijavaRepository.findById(request.prijavaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Prijava ne postoji"
                ));

        Grupa grupa = grupaRepository.findById(request.grupaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Grupa ne postoji"
                ));

        if (!prijava.getStudentIndeks().isAktivan()) {
            throw new BadRequestException(
                    "Studentski indeks nije aktivan"
            );
        }

        boolean odgovaraIspit = prijava.getIspit().getId()
                .equals(grupa.getTermin().getIspit().getId());

        if (!odgovaraIspit) {
            throw new BadRequestException(
                    "Grupa ne pripada ispitu za koji je student prijavljen"
            );
        }

        if (radRepository.existsByPrijava_Id(prijava.getId())) {
            throw new ConflictException(
                    "Za ovu prijavu vec postoji rad"
            );
        }

        Rad rad = new Rad();
        rad.setPrijava(prijava);
        rad.setGrupa(grupa);
        rad.setUcionica(request.ucionica());
        rad.setStatusRada(StatusRada.PREUZET);

        try {
            return radRepository.saveAndFlush(rad);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Rad nije moguce sacuvati zbog duplikata ili povezanih podataka"
            );
        }
    }

    @Transactional
    public Rad update(Long id, RadUpdateRequest request) {
        Rad rad = findByIdForUpdateOrThrow(id);
        rad.setUcionica(request.ucionica());

        try {
            return radRepository.saveAndFlush(rad);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Rad nije moguce izmeniti zbog konflikta podataka"
            );
        }
    }

    @Transactional
    public Rad oceni(Long id, RadOcenaRequest request) {
        Rad rad = findByIdForUpdateOrThrow(id);

        if (rad.getStatusRada() != StatusRada.PREDAT) {
            throw new ConflictException(
                    "Moguce je oceniti samo konacno predat rad"
            );
        }

        if (request.poeni().signum() < 0
                || request.poeni().compareTo(new BigDecimal("130")) > 0
                || request.poeni().stripTrailingZeros().scale() > 1) {
            throw new BadRequestException(
                    "Poeni moraju biti od 0 do 130 sa najvise jednom decimalom"
            );
        }

        Nastavnik nastavnik = nastavnikRepository.findById(request.nastavnikId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nastavnik ne postoji"
                ));

        rad.setPoeni(request.poeni());
        rad.setOcenioNastavnik(nastavnik);
        rad.setVremeOcenjivanja(LocalDateTime.now());

        try {
            return radRepository.saveAndFlush(rad);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Ocenu nije moguce sacuvati zbog konflikta podataka"
            );
        }
    }

    public List<Rad> findAll() {
        return radRepository.findAll();
    }

    public Page<Rad> findAll(Pageable pageable) {
        return radRepository.findAll(pageable);
    }

    public Rad findById(Long id) {
        return radRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Rad sa id " + id + " ne postoji"
                ));
    }

    public Page<Rad> findByIspit(Long ispitId, Pageable pageable) {
        if (!ispitRepository.existsById(ispitId)) {
            throw new ResourceNotFoundException("Ispit ne postoji");
        }

        return radRepository.findAllByPrijava_Ispit_Id(
                ispitId,
                pageable
        );
    }

    @Transactional
    public void deleteById(Long id) {
        Rad rad = findByIdForUpdateOrThrow(id);

        if (rad.getStatusRada() == StatusRada.PREDAT) {
            throw new ConflictException(
                    "Konacno predat rad nije moguce obrisati"
            );
        }

        try {
            radRepository.delete(rad);
            radRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Rad nije moguce obrisati zbog povezanih podataka"
            );
        }
    }

    private Rad findByIdForUpdateOrThrow(long id) {
        return radRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Rad sa id " + id + " ne postoji"
                ));
    }
}