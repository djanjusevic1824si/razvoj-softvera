package org.example.razvojsoftvera.services;

import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.exceptions.BadRequestException;
import org.example.razvojsoftvera.exceptions.ConflictException;
import org.example.razvojsoftvera.exceptions.ResourceNotFoundException;
import org.example.razvojsoftvera.model.IspitniRok;
import org.example.razvojsoftvera.repositories.IspitniRokRepository;
import org.example.razvojsoftvera.repositories.TerminRepository;
import org.example.razvojsoftvera.requests.IspitniRokCreateRequest;
import org.example.razvojsoftvera.requests.IspitniRokUpdateRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@PreAuthorize("hasRole('NASTAVNIK')")
public class IspitniRokService {

    private final IspitniRokRepository ispitniRokRepository;
    private final TerminRepository terminRepository;

    @Transactional
    public IspitniRok save(IspitniRokCreateRequest request) {
        if (request.datumDo().isBefore(request.datumOd())) {
            throw new BadRequestException(
                    "Datum do ne sme biti pre datuma od"
            );
        }

        boolean postoji = ispitniRokRepository.existsByNazivAndSkolskaGodina(
                request.naziv().trim(),
                request.skolskaGodina().trim()
        );

        if (postoji) {
            throw new ConflictException(
                    "Rok sa tim nazivom i skolskom godinom vec postoji"
            );
        }

        IspitniRok ispitniRok = new IspitniRok();
        ispitniRok.setNaziv(request.naziv().trim());
        ispitniRok.setSkolskaGodina(request.skolskaGodina().trim());
        ispitniRok.setDatumOd(request.datumOd());
        ispitniRok.setDatumDo(request.datumDo());

        try {
            return ispitniRokRepository.saveAndFlush(ispitniRok);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Ispitni rok nije moguce sacuvati zbog konflikta sa podacima u bazi"
            );
        }
    }

    @Transactional
    public IspitniRok update(
            Long id,
            IspitniRokUpdateRequest request
    ) {
        IspitniRok ispitniRok = findByIdOrThrow(id);

        if (request.datumDo().isBefore(request.datumOd())) {
            throw new BadRequestException(
                    "Datum do ne sme biti pre datuma od"
            );
        }

        boolean postoji = ispitniRokRepository.existsByNazivAndSkolskaGodinaAndIdNot(
                request.naziv().trim(),
                request.skolskaGodina().trim(),
                id
        );

        if (postoji) {
            throw new ConflictException(
                    "Rok sa tim nazivom i skolskom godinom vec postoji"
            );
        }

        boolean postojeTerminiVanRoka = terminRepository.postojiVanRoka(
                id,
                request.datumOd().atStartOfDay(),
                request.datumDo().plusDays(1).atStartOfDay()
        );

        if (postojeTerminiVanRoka) {
            throw new ConflictException(
                    "Novi datumi roka ne obuhvataju postojece termine"
            );
        }

        ispitniRok.setNaziv(request.naziv().trim());
        ispitniRok.setSkolskaGodina(request.skolskaGodina().trim());
        ispitniRok.setDatumOd(request.datumOd());
        ispitniRok.setDatumDo(request.datumDo());

        try {
            return ispitniRokRepository.saveAndFlush(ispitniRok);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Ispitni rok nije moguce izmeniti zbog konflikta sa podacima u bazi"
            );
        }
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'NASTAVNIK')")
    public List<IspitniRok> findAll() {
        return ispitniRokRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'NASTAVNIK')")
    public Page<IspitniRok> findAll(Pageable pageable) {
        return ispitniRokRepository.findAll(pageable);
    }

    public IspitniRok findById(Long id) {
        return findByIdOrThrow(id);
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'NASTAVNIK')")
    public Page<IspitniRok> findBySkolskaGodina(
            String godina,
            Pageable pageable
    ) {
        return ispitniRokRepository.findAllBySkolskaGodina(
                godina.trim(),
                pageable
        );
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'NASTAVNIK')")
    public Page<IspitniRok> search(
            String naziv,
            Pageable pageable
    ) {
        return ispitniRokRepository.findAllByNazivContainingIgnoreCase(
                naziv.trim(),
                pageable
        );
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'NASTAVNIK')")
    public List<IspitniRok> findAktivni(LocalDate datum) {
        return ispitniRokRepository
                .findAllByDatumOdLessThanEqualAndDatumDoGreaterThanEqualOrderByDatumOdAsc(
                        datum,
                        datum);

    }

    @Transactional
    public void deleteById(Long id) {
        IspitniRok ispitniRok = findByIdOrThrow(id);

        try {
            ispitniRokRepository.delete(ispitniRok);
            ispitniRokRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Ispitni rok ne moze da se obrise jer postoje ispiti koji mu pripadaju"
            );
        }
    }

    private IspitniRok findByIdOrThrow(Long id){
        return ispitniRokRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ispitni rok sa id " + id + " ne postoji"));
    }
}