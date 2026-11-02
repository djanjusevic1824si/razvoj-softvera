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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NastavnikService {

    private final NastavnikRepository nastavnikRepository;
    private final KorisnikRepository korisnikRepository;

    @Transactional
    public Nastavnik save(Nastavnik nastavnik, Long korisnikId) {
        if (korisnikId == null) {
            throw new BadRequestException(
                    "Za nastavnika je obavezan korisnikId"
            );
        }

        Korisnik korisnik = korisnikRepository.findById(korisnikId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Korisnik sa id " + korisnikId + " ne postoji"
                ));

        if (korisnik.getUloga() != Uloga.NASTAVNIK) {
            throw new BadRequestException(
                    "Korisnik mora imati ulogu NASTAVNIK"
            );
        }
        if (nastavnikRepository.existsByKorisnikId(korisnikId)) {
            throw new ConflictException(
                    "Korisnik je vec povezan sa nastavnikom"
            );
        }

        nastavnik.setKorisnik(korisnik);

        try {
            return nastavnikRepository.saveAndFlush(nastavnik);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Nastavnik nije sacuvan zbog konflikta podataka"
            );
        }
    }

    public List<Nastavnik> findAll() {
        return nastavnikRepository.findAll();
    }

    public Nastavnik findById(Long id) {
        return nastavnikRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nastavnik sa id " + id + " ne postoji"
                ));
    }

    public List<Nastavnik> findByImeAndPrezime(String ime, String prezime) {
        return nastavnikRepository.findByImeAndPrezime(ime, prezime);
    }
}
