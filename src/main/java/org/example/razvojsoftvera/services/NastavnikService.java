package org.example.razvojsoftvera.services;

import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.exceptions.BadRequestException;
import org.example.razvojsoftvera.exceptions.ConflictException;
import org.example.razvojsoftvera.exceptions.ResourceNotFoundException;
import org.example.razvojsoftvera.model.Korisnik;
import org.example.razvojsoftvera.model.Nastavnik;
import org.example.razvojsoftvera.model.Uloga;
import org.example.razvojsoftvera.repositories.KorisnikRepository;
import org.example.razvojsoftvera.repositories.NastavnikRepository;
import org.example.razvojsoftvera.requests.NastavnikRequest;
import org.example.razvojsoftvera.requests.NastavnikUpdateRequest;
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
public class NastavnikService {

    private final NastavnikRepository nastavnikRepository;
    private final KorisnikRepository korisnikRepository;

    @Transactional
    public Nastavnik save(NastavnikRequest request) {
        if (request.korisnikId() == null) {
            throw new BadRequestException(
                    "Za nastavnika je obavezan korisnikId"
            );
        }

        Korisnik korisnik = korisnikRepository.findById(request.korisnikId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Korisnik sa id " + request.korisnikId() + " ne postoji"
                ));

        if (korisnik.getUloga() != Uloga.NASTAVNIK) {
            throw new BadRequestException(
                    "Korisnik mora imati ulogu NASTAVNIK"
            );
        }

        if (nastavnikRepository.existsByKorisnikId(request.korisnikId())) {
            throw new ConflictException(
                    "Korisnik je vec povezan sa nastavnikom"
            );
        }

        Nastavnik nastavnik = new Nastavnik();
        nastavnik.setIme(request.ime().trim());
        nastavnik.setPrezime(request.prezime().trim());
        nastavnik.setZvanje(
                request.zvanje() == null ? null : request.zvanje().trim()
        );
        nastavnik.setKorisnik(korisnik);

        try {
            return nastavnikRepository.saveAndFlush(nastavnik);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Nastavnika nije moguce sacuvati zbog duplikata ili konflikta povezanih podataka"
            );
        }
    }

    @Transactional
    public Nastavnik update(Long id, NastavnikUpdateRequest request) {
        Nastavnik nastavnik = findByIdOrThrow(id);

        nastavnik.setIme(request.ime().trim());
        nastavnik.setPrezime(request.prezime().trim());
        nastavnik.setZvanje(
                request.zvanje() == null ? null : request.zvanje().trim()
        );

        try {
            return nastavnikRepository.saveAndFlush(nastavnik);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Nastavnika nije moguce izmeniti zbog konflikta podataka"
            );
        }
    }

    public List<Nastavnik> findAll() {
        return nastavnikRepository.findAll();
    }

    public Page<Nastavnik> findAll(Pageable pageable) {
        return nastavnikRepository.findAll(pageable);
    }

    public Nastavnik findById(Long id) {
        return findByIdOrThrow(id);
    }

    public Nastavnik findByKorisnikId(Long korisnikId) {
        return nastavnikRepository.findByKorisnikId(korisnikId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nastavnik za korisnika sa id " + korisnikId + " ne postoji"
                ));
    }

    public List<Nastavnik> findByImeAndPrezime(String ime, String prezime) {
        String imePretraga = ime == null ? "" : ime.trim();
        String prezimePretraga = prezime == null ? "" : prezime.trim();

        return nastavnikRepository
                .findByImeContainingIgnoreCaseAndPrezimeContainingIgnoreCase(
                        imePretraga,
                        prezimePretraga
                );
    }

    public Page<Nastavnik> findByImeAndPrezime(
            String ime,
            String prezime,
            Pageable pageable
    ) {
        String imePretraga = ime == null ? "" : ime.trim();
        String prezimePretraga = prezime == null ? "" : prezime.trim();

        return nastavnikRepository
                .findByImeContainingIgnoreCaseAndPrezimeContainingIgnoreCase(
                        imePretraga,
                        prezimePretraga,
                        pageable
                );
    }

    @Transactional
    public void deleteById(Long id) {
        Nastavnik nastavnik = findByIdOrThrow(id);

        try {
            nastavnikRepository.delete(nastavnik);
            nastavnikRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Nastavnik ne moze da se obrise jer postoje povezani podaci"
            );
        }
    }

    private Nastavnik findByIdOrThrow(Long id) {
        return nastavnikRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nastavnik sa id " + id + " ne postoji"
                ));
    }
}