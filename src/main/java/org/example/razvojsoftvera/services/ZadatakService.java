package org.example.razvojsoftvera.services;

import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.exceptions.ConflictException;
import org.example.razvojsoftvera.exceptions.ResourceNotFoundException;
import org.example.razvojsoftvera.model.Nastavnik;
import org.example.razvojsoftvera.model.Zadatak;
import org.example.razvojsoftvera.repositories.NastavnikRepository;
import org.example.razvojsoftvera.repositories.ZadatakRepository;
import org.example.razvojsoftvera.requests.ZadatakCreateRequest;
import org.example.razvojsoftvera.requests.ZadatakUpdateRequest;
import org.springframework.core.io.Resource;
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
public class ZadatakService {

    private final ZadatakRepository zadatakRepository;
    private final NastavnikRepository nastavnikRepository;
    private final SkladisteFajlova skladisteFajlova;

    @Transactional
    public Zadatak save(ZadatakCreateRequest request) {
        Nastavnik nastavnik = nastavnikRepository
                .findById(request.nastavnikId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nastavnik sa id " + request.nastavnikId() + " ne postoji"
                ));

        String putanja = skladisteFajlova.sacuvaj(request.fajl(), "zadaci");

        Zadatak zadatak = new Zadatak();
        zadatak.setNaziv(request.naziv().trim());
        zadatak.setOpis(request.opis());
        zadatak.setPutanjaZadatka(putanja);
        zadatak.setNastavnik(nastavnik);

        try {
            return zadatakRepository.saveAndFlush(zadatak);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Zadatak nije moguce sacuvati zbog konflikta sa podacima u bazi"
            );
        }
    }

    @Transactional
    public Zadatak update(Long id, ZadatakUpdateRequest request) {
        Zadatak zadatak = getEntity(id);

        Nastavnik nastavnik = nastavnikRepository
                .findById(request.nastavnikId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nastavnik sa id " + request.nastavnikId() + " ne postoji"
                ));

        zadatak.setNaziv(request.naziv().trim());
        zadatak.setOpis(request.opis());
        zadatak.setNastavnik(nastavnik);

        try {
            return zadatakRepository.saveAndFlush(zadatak);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Zadatak nije moguce izmeniti zbog konflikta sa podacima u bazi"
            );
        }
    }

    public List<Zadatak> findAll() {
        return zadatakRepository.findAll();
    }

    public Page<Zadatak> findAll(Pageable pageable) {
        return zadatakRepository.findAll(pageable);
    }

    public Zadatak findById(Long id) {
        return getEntity(id);
    }

    public Page<Zadatak> findByNastavnik(
            Long nastavnikId,
            Pageable pageable
    ) {
        if (!nastavnikRepository.existsById(nastavnikId)) {
            throw new ResourceNotFoundException(
                    "Nastavnik sa id " + nastavnikId + " ne postoji"
            );
        }

        return zadatakRepository.findAllByNastavnik_Id(nastavnikId, pageable);
    }

    public Page<Zadatak> search(
            String naziv,
            Pageable pageable
    ) {
        return zadatakRepository.findAllByNazivContainingIgnoreCase(
                naziv.trim(),
                pageable
        );
    }

    @Transactional
    public void deleteById(Long id) {
        Zadatak zadatak = getEntity(id);

        try {
            zadatakRepository.delete(zadatak);
            zadatakRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Zadatak ne moze da se obrise jer ga koriste postojece grupe"
            );
        }
    }

    private Zadatak getEntity(Long id) {
        return zadatakRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Zadatak sa id " + id + " ne postoji"
                ));
    }

    public Resource preuzmiProjekat(Long id){
        Zadatak zadatak = getEntity(id);
        return skladisteFajlova.ucitaj(zadatak.getPutanjaZadatka());
    }
}