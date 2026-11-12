package org.example.razvojsoftvera.services;

import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.exceptions.ResourceNotFoundException;
import org.example.razvojsoftvera.exceptions.UnauthorizedException;
import org.example.razvojsoftvera.repositories.PrijavaRepository;
import org.example.razvojsoftvera.repositories.RadRepository;
import org.example.razvojsoftvera.repositories.StudentIndeksRepository;
import org.example.razvojsoftvera.repositories.StudentRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("pristupService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PristupService {

    private final StudentRepository studentRepository;
    private final StudentIndeksRepository studentIndeksRepository;
    private final PrijavaRepository prijavaRepository;
    private final RadRepository radRepository;

    public boolean jeSvojProfil(
            Long studentId,
            Authentication authentication
    ) {
        Long korisnikId = korisnikIdIzTokena(authentication);

        if (studentId == null || korisnikId == null) {
            return false;
        }

        return studentRepository.existsByIdAndKorisnik_Id(
                studentId,
                korisnikId
        );
    }

    public boolean jeSvojeResenje(
            Long ispitId,
            Authentication authentication
    ){
        Long korisnikId = korisnikIdIzTokena(authentication);

        if(korisnikId == null)
            return false;

        return radRepository.existsZaIspitIKorisnika(ispitId, korisnikId);
    }

    public boolean jeSvojIndeks(
            Long indeksId,
            Authentication authentication
    ) {
        Long korisnikId = korisnikIdIzTokena(authentication);

        if (indeksId == null || korisnikId == null) {
            return false;
        }

        return studentIndeksRepository.existsByIdAndStudent_Korisnik_Id(
                indeksId,
                korisnikId
        );
    }

    public boolean jeSvojaPrijava(
            Long prijavaId,
            Authentication authentication
    ) {
        Long korisnikId = korisnikIdIzTokena(authentication);

        if (prijavaId == null || korisnikId == null) {
            return false;
        }

        return prijavaRepository
                .existsByIdAndStudentIndeks_Student_Korisnik_Id(
                        prijavaId,
                        korisnikId
                );
    }

    private Long korisnikIdIzTokena(Authentication authentication) {
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthentication)
                || !authentication.isAuthenticated()) {
            return null;
        }

        boolean student = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_STUDENT")
                );

        if (!student) {
            return null;
        }

        Object claim = jwtAuthentication
                .getToken()
                .getClaims()
                .get("korisnikId");

        if (claim == null) {
            return null;
        }

        try {
            long korisnikId = Long.parseLong(claim.toString());

            return korisnikId > 0 ? korisnikId : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }
    
    public Long ulogovaniStudentKorisnikId(){
        Long korisnikId = korisnikIdIzTokena(
                SecurityContextHolder.getContext().getAuthentication()
        );
        if (korisnikId == null) {
            throw new UnauthorizedException("Niste ulogovani kao student");
        }

        return korisnikId;
    }

    public Long studentId(Authentication authentication) {
        Long korisnikId = korisnikIdIzTokena(authentication);
        if (korisnikId == null) {
            throw new ResourceNotFoundException("Korisnik ne postoji");
        }
        return studentRepository.findIdByKorisnikId(korisnikId)
                .orElseThrow(() -> new UnauthorizedException("Korisnik nije student"));
    }
}