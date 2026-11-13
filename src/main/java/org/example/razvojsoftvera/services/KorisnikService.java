package org.example.razvojsoftvera.services;

import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.exceptions.BadRequestException;
import org.example.razvojsoftvera.exceptions.ConflictException;
import org.example.razvojsoftvera.exceptions.ResourceNotFoundException;
import org.example.razvojsoftvera.model.Korisnik;
import org.example.razvojsoftvera.model.Uloga;
import org.example.razvojsoftvera.repositories.KorisnikRepository;
import org.example.razvojsoftvera.requests.KorisnikPasswordRequest;
import org.example.razvojsoftvera.requests.KorisnikRequest;
import org.example.razvojsoftvera.requests.KorisnikUpdateRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@PreAuthorize("hasRole('NASTAVNIK')")
public class KorisnikService {

    private final KorisnikRepository korisnikRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Korisnik save(KorisnikRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        if (korisnikRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException(
                    "Nalog sa tim email-om vec postoji"
            );
        }

        if (request.lozinka() == null || request.lozinka().isBlank()) {
            throw new BadRequestException(
                    "Lozinka ne sme biti prazna"
            );
        }

        if (request.lozinka().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new BadRequestException(
                    "BCrypt lozinka moze imati najvise 72 bajta"
            );
        }

        if (request.uloga() == null) {
            throw new BadRequestException(
                    "Uloga je obavezna"
            );
        }

        Korisnik korisnik = new Korisnik();
        korisnik.setEmail(email);
        korisnik.setLozinka(passwordEncoder.encode(request.lozinka()));
        korisnik.setUloga(request.uloga());

        try {
            return korisnikRepository.saveAndFlush(korisnik);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Korisnika nije moguce sacuvati zbog duplikata ili konflikta podataka"
            );
        }
    }

    @Transactional
    public Korisnik update(Long id, KorisnikUpdateRequest request) {
        Korisnik korisnik = findByIdOrThrow(id);

        String email = request.email().trim().toLowerCase(Locale.ROOT);

        if (korisnikRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new ConflictException(
                    "Nalog sa tim email-om vec postoji"
            );
        }

        korisnik.setEmail(email);

        try {
            return korisnikRepository.saveAndFlush(korisnik);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Korisnika nije moguce izmeniti zbog duplikata ili konflikta podataka"
            );
        }
    }

    public List<Korisnik> findAll() {
        return korisnikRepository.findAll();
    }

    public Page<Korisnik> findAll(Pageable pageable) {
        return korisnikRepository.findAll(pageable);
    }

    public Korisnik findById(Long id) {
        return findByIdOrThrow(id);
    }

    public Page<Korisnik> findByUloga(Uloga uloga, Pageable pageable) {
        return korisnikRepository.findByUloga(uloga, pageable);
    }

    @Transactional
    public void resetPassword(Long id, KorisnikPasswordRequest request) {
        Korisnik korisnik = findByIdOrThrow(id);

        if (request.novaLozinka() == null || request.novaLozinka().isBlank()) {
            throw new BadRequestException(
                    "Lozinka ne sme biti prazna"
            );
        }

        if (request.novaLozinka().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new BadRequestException(
                    "BCrypt lozinka moze imati najvise 72 bajta"
            );
        }

        korisnik.setLozinka(
                passwordEncoder.encode(request.novaLozinka())
        );

        try {
            korisnikRepository.saveAndFlush(korisnik);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Lozinku nije moguce izmeniti zbog konflikta podataka"
            );
        }
    }

    @Transactional
    public void deleteById(Long id) {
        Korisnik korisnik = findByIdOrThrow(id);

        try {
            korisnikRepository.delete(korisnik);
            korisnikRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Korisnik ne moze da se obrise jer je povezan sa studentom ili nastavnikom"
            );
        }
    }

    private Korisnik findByIdOrThrow(Long id) {
        return korisnikRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Korisnik sa id " + id + " ne postoji"
                ));
    }
}