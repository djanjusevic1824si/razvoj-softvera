package org.example.razvojsoftvera.services;

import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.exceptions.BadRequestException;
import org.example.razvojsoftvera.exceptions.ConflictException;
import org.example.razvojsoftvera.exceptions.ResourceNotFoundException;
import org.example.razvojsoftvera.model.Korisnik;
import org.example.razvojsoftvera.model.Uloga;
import org.example.razvojsoftvera.repositories.KorisnikRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KorisnikService {

    private final KorisnikRepository korisnikRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Korisnik save(String email, String lozinka, Uloga uloga) {
        String normalizedEmail = email.strip().toLowerCase(Locale.ROOT);

        if (korisnikRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ConflictException(
                    "Nalog sa tim email-om vec postoji"
            );
        }
        if (lozinka.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new BadRequestException(
                    "BCrypt lozinka moze imati najvise 72 bajta"
            );
        }

        Korisnik korisnik = new Korisnik();
        korisnik.setEmail(normalizedEmail);
        korisnik.setLozinka(passwordEncoder.encode(lozinka));
        korisnik.setUloga(uloga);

        try {
            return korisnikRepository.saveAndFlush(korisnik);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Nalog nije sacuvan zbog konflikta podataka"
            );
        }
    }

    public List<Korisnik> findAll() {
        return korisnikRepository.findAll();
    }

    public Korisnik findById(Long id) {
        return korisnikRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Korisnik sa id " + id + " ne postoji"
                ));
    }
}
