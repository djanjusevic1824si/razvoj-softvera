package org.example.razvojsoftvera.services;

import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.exceptions.*;
import org.example.razvojsoftvera.model.*;
import org.example.razvojsoftvera.repositories.*;
import org.example.razvojsoftvera.requests.*;
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
public class KomentarService {

    private final KomentarRepository komentarRepository;
    private final RadRepository radRepository;
    private final NastavnikRepository nastavnikRepository;

    @Transactional
    public Komentar save(KomentarCreateRequest request) {
        if (request.linijaOd() < 1
                || request.linijaDo() < request.linijaOd()) {
            throw new BadRequestException(
                    "Linija od mora biti pozitivna, a linija do ne sme biti manja od nje"
            );
        }

        Rad rad = radRepository.findById(request.radId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Rad ne postoji"
                ));

        Nastavnik nastavnik = nastavnikRepository.findById(request.nastavnikId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nastavnik ne postoji"
                ));

        Komentar komentar = new Komentar();
        komentar.setRad(rad);
        komentar.setNastavnik(nastavnik);
        komentar.setPutanjaFajla(request.putanjaFajla().trim());
        komentar.setLinijaOd(request.linijaOd());
        komentar.setLinijaDo(request.linijaDo());
        komentar.setTekst(request.tekst().trim());

        try {
            return komentarRepository.saveAndFlush(komentar);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Komentar nije moguce sacuvati zbog konflikta povezanih podataka"
            );
        }
    }

    @Transactional
    public Komentar update(Long id, KomentarUpdateRequest request) {
        Komentar komentar = findByIdOrThrow(id);

        if (request.linijaOd() < 1
                || request.linijaDo() < request.linijaOd()) {
            throw new BadRequestException(
                    "Linija od mora biti pozitivna, a linija do ne sme biti manja od nje"
            );
        }

        komentar.setPutanjaFajla(request.putanjaFajla().trim());
        komentar.setLinijaOd(request.linijaOd());
        komentar.setLinijaDo(request.linijaDo());
        komentar.setTekst(request.tekst().trim());

        try {
            return komentarRepository.saveAndFlush(komentar);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Komentar nije moguce izmeniti zbog konflikta podataka"
            );
        }
    }

    public List<Komentar> findAll() {
        return komentarRepository.findAll();
    }

    public Page<Komentar> findAll(Pageable pageable) {
        return komentarRepository.findAll(pageable);
    }

    public Komentar findById(Long id) {
        return findByIdOrThrow(id);
    }

    public Page<Komentar> findByRad(Long radId, Pageable pageable) {
        if (!radRepository.existsById(radId)) {
            throw new ResourceNotFoundException("Rad ne postoji");
        }

        return komentarRepository.findAllByRad_Id(radId, pageable);
    }

    @Transactional
    public void deleteById(Long id) {
        Komentar komentar = findByIdOrThrow(id);

        try {
            komentarRepository.delete(komentar);
            komentarRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Komentar nije moguce obrisati zbog povezanih podataka"
            );
        }
    }

    private Komentar findByIdOrThrow(long id) {
        return komentarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Komentar sa id " + id + " ne postoji"
                ));
    }
}