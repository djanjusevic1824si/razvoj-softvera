package org.example.razvojsoftvera.services;

import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.exceptions.ResourceNotFoundException;
import org.example.razvojsoftvera.model.Korisnik;
import org.example.razvojsoftvera.repositories.KorisnikRepository;
import org.example.razvojsoftvera.requests.LoginRequest;
import org.example.razvojsoftvera.responses.LoginResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final long TOKEN_TRAJANJE_SEKUNDE = 900;
    private final KorisnikRepository korisnikRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String email = request.email().strip().toLowerCase(Locale.ROOT);

        Korisnik korisnik = korisnikRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Neispravan email ili lozinka"));

        if (!passwordEncoder.matches(
                request.lozinka(),
                korisnik.getLozinka()
        )) {
            throw new ResourceNotFoundException(
                    "Neispravan email ili lozinka"
            );
        }

        Instant sada = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("razvoj-softvera")
                .subject(korisnik.getId().toString())
                .issuedAt(sada)
                .expiresAt(sada.plusSeconds(TOKEN_TRAJANJE_SEKUNDE))
                .claim("uloga", korisnik.getUloga().name())
                .claim("korisnikId", korisnik.getId())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        String token = jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();

        return new LoginResponse(
                token,
                "Bearer",
                TOKEN_TRAJANJE_SEKUNDE,
                korisnik.getId(),
                korisnik.getUloga()
        );
    }
}
