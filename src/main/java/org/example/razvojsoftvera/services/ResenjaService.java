package org.example.razvojsoftvera.services;

import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.exceptions.BadRequestException;
import org.example.razvojsoftvera.exceptions.ResourceNotFoundException;
import org.example.razvojsoftvera.model.*;
import org.example.razvojsoftvera.repositories.NastavnikRepository;
import org.example.razvojsoftvera.repositories.PredajaRepository;
import org.example.razvojsoftvera.repositories.RadRepository;
import org.example.razvojsoftvera.requests.OcenaRequest;
import org.example.razvojsoftvera.responses.ResenjeResponse;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
@PreAuthorize("hasRole('NASTAVNIK')")
public class ResenjaService {

    private final PredajaRepository predajaRepository;
    private final NastavnikRepository nastavnikRepository;
    private final RadRepository radRepository;
    private final PristupService pristupService;
    private final SkladisteFajlova skladiste;

    public record StavkaZipa(String imeUZipu, Resource fajl) {}
    public record SadrzajZipa(List<StavkaZipa> fajlovi, List<String> nedostaju) {}

    public List<ResenjeResponse> sve(Long ispitId, Long grupaId) {
        return predajaRepository.konacnePredajeSve(ispitId, grupaId);
    }

    @Transactional
    public Rad oceni(Long predajaId, OcenaRequest z) {
        Predaja predaja = predajaRepository.findById(predajaId)
                .orElseThrow(() -> new ResourceNotFoundException("Predaja sa id " + predajaId + " ne postoji"));

        if (predaja.getTipPredaje() != TipPredaje.KONACNA)
            throw new BadRequestException("Ocenjuje se samo konačna predaja");

        Rad rad = predaja.getRad();
        if (rad.getStatusRada() != StatusRada.PREDAT)
            throw new BadRequestException("Rad nije predat");

        if (z.poeni().signum() < 0
                || z.poeni().compareTo(new BigDecimal("130")) > 0
                || z.poeni().stripTrailingZeros().scale() > 1) {
            throw new BadRequestException(
                    "Poeni moraju biti od 0 do 130 sa najvise jednom decimalom"
            );
        }

        Long nastavnikId = pristupService.nastavnikId(
                SecurityContextHolder.getContext().getAuthentication());
        Nastavnik nastavnik = nastavnikRepository.findById(nastavnikId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nastavnik ne postoji"
                ));
        rad.setPoeni(z.poeni());
        rad.setOcenioNastavnik(nastavnik);
        rad.setVremeOcenjivanja(LocalDateTime.now());

        return rad;
    }

    @PreAuthorize("""
        hasRole('NASTAVNIK')
        or (
            hasRole('STUDENT')
            and @pristupService.jeSvojeResenje(#p0, authentication)
        )
        """)
    public Rad mojeResenje(Long ispitId){
        Long studentId = pristupService.studentId(SecurityContextHolder.getContext().getAuthentication());
        return radRepository.findByIspitIdAndStudentId(ispitId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Ne postoji rad trazenog studenta"));
    }


    public SadrzajZipa pripremiZip(Long ispitId, Long grupaId) {
        List<StavkaZipa> fajlovi = new ArrayList<>();
        List<String> nedostaju = new ArrayList<>();

        for (ResenjeResponse r : predajaRepository.konacnePredajeSve(ispitId, grupaId)) {
            String folder = r.grupa() + "/" + r.studijskiProgram() + "_" + r.broj() + "_"
                    + r.godinaUpisa() + "_" + r.prezime() + "_" + r.ime();
            try {
                fajlovi.add(new StavkaZipa(
                        folder + "/resenje-" + r.predajaId() + ".zip",
                        skladiste.ucitaj(r.putanjaResenja())));
            } catch (ResourceNotFoundException e) {
                nedostaju.add(folder);
            }
        }

        if (fajlovi.isEmpty() && nedostaju.isEmpty()) {
            throw new ResourceNotFoundException("Nema predatih resenja za zadate kriterijume");
        }
        return new SadrzajZipa(fajlovi, nedostaju);
    }
}
