package org.example.razvojsoftvera.services;

import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.exceptions.BadRequestException;
import org.example.razvojsoftvera.exceptions.ConflictException;
import org.example.razvojsoftvera.exceptions.ForbiddenException;
import org.example.razvojsoftvera.exceptions.ResourceNotFoundException;
import org.example.razvojsoftvera.model.*;
import org.example.razvojsoftvera.repositories.*;
import org.example.razvojsoftvera.requests.PolaganjeStartRequest;
import org.example.razvojsoftvera.requests.PredajaCreateRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@PreAuthorize("hasRole('STUDENT')")
public class PolaganjeService {

    private final TerminRepository terminRepository;
    private final GrupaRepository grupaRepository;
    private final StudentIndeksRepository studentIndeksRepository;
    private final PrijavaRepository prijavaRepository;
    private final RadRepository radRepository;
    private final PristupService pristupService;
    private final SkladisteFajlova skladiste;
    private final PredajaRepository predajaRepository;
    @Value("${app.predaja.tolerancija}")
    private Duration tolerancija;


    @Transactional
    public Rad start(PolaganjeStartRequest request){
        Termin termin = terminRepository.findSaPredmetomById(request.terminId())
                .orElseThrow(() -> new ResourceNotFoundException("Ne postoji termin sa zadatim id-jem"));

        Grupa grupa = grupaRepository.findById(request.grupaId())
                .orElseThrow(() -> new ResourceNotFoundException("Ne postoji grupa sa zadatim id-jem"));

        if (!grupa.getTermin().getId().equals(termin.getId())){
            throw new BadRequestException("Grupa ne pripada izabranom terminu");
        }

        proveriDaTerminTraje(termin);

        StudentIndeks studentIndeks = aktivanIndeks();

        Prijava prijava = prijavaRepository
                .findByStudentIndeks_IdAndIspit_Id(studentIndeks.getId(), termin.getIspit().getId()).
                orElseThrow(() -> new ForbiddenException("Niste na spisku za polaganje ovog predmeta"));

        Optional<Rad> postojeci = radRepository.findByPrijava_Id(prijava.getId());

        if (postojeci.isPresent()){
            Rad rad = postojeci.get();

            if (rad.getStatusRada().equals(StatusRada.PREDAT)){
                throw new ConflictException("Rad je vec predat, ponovni pristup nije moguc");
            }

            if (!rad.getGrupa().getId().equals(grupa.getId())){
                throw new ConflictException("Polaganje ste vec zapoceli u drugoj grupi");
            }
            return rad;
        }

        Rad rad = new Rad();
        rad.setGrupa(grupa);
        rad.setPrijava(prijava);
        rad.setUcionica(request.ucionica());
        rad.setStatusRada(StatusRada.PREUZET);

        try{
            return radRepository.saveAndFlush(rad);
        }catch (DataIntegrityViolationException e){
            throw new ConflictException("Polaganje je upravo zapoceto, pokusajte ponovo");
        }
    }

    public Resource preuzmiProjekat(Long radId){
        Rad rad = radRepository.findById(radId)
                .orElseThrow(() -> new ResourceNotFoundException("Rad ne postoj"));

        StudentIndeks studentIndeks = aktivanIndeks();

        if(!rad.getPrijava().getStudentIndeks().getId().equals(studentIndeks.getId())){
            throw new ForbiddenException("Ovo nije Vas projekat");
        }

        if(rad.getStatusRada() == StatusRada.PREDAT){
            throw new ConflictException("Rad je vec predat");
        }

        proveriDaTerminTraje(rad.getGrupa().getTermin());

        Optional<Predaja> poslednja = predajaRepository.findFirstByRad_IdOrderByRedniBrojDesc(rad.getId());

        if (poslednja.isPresent()){
            return skladiste.ucitaj(poslednja.get().getPutanjaResenja());
        }
        return skladiste.ucitaj(rad.getGrupa().getZadatak().getPutanjaZadatka());
    }

    @Transactional
    public Predaja predaja(Long radId, PredajaCreateRequest request){
        Rad rad = radRepository.findByIdForUpdate(radId)
                .orElseThrow(() -> new ResourceNotFoundException("Ne postoji rad sa zadatim ID-jem"));

        StudentIndeks indeks = aktivanIndeks();

        if (!rad.getPrijava().getStudentIndeks().getId().equals(indeks.getId())){
            throw new ForbiddenException("Ovo nije Vas rad");
        }

        if(rad.getStatusRada().equals(StatusRada.PREDAT)){
            throw new ConflictException("Rad je vec predat");
        }

        proveriDaJePredajaDozvoljena(rad.getGrupa().getTermin());

        String putanja = skladiste.sacuvaj(request.fajl(), "predaje");
        int redniBroj = predajaRepository.findMaxRedniBrojByRadId(rad.getId()) + 1;

        Predaja predaja = new Predaja();
        predaja.setPutanjaResenja(putanja);
        predaja.setRad(rad);
        predaja.setRedniBroj(redniBroj);
        predaja.setTipPredaje(request.tipPredaje());

        if (request.tipPredaje().equals(TipPredaje.KONACNA)){
            rad.setStatusRada(StatusRada.PREDAT);
        }

        return predajaRepository.saveAndFlush(predaja);
    }

    private void proveriDaTerminTraje(Termin termin){
        LocalDateTime sada = LocalDateTime.now();

        if (sada.isBefore(termin.getPocetakIzrade())){
            throw new ConflictException("Termin jos nije poceo");
        }
        if (!sada.isBefore(termin.getKrajIzrade())){
            throw new ConflictException("Termin je zavrsen");
        }
    }

    private StudentIndeks aktivanIndeks(){
        Long korisnikId = pristupService.ulogovaniStudentKorisnikId();

        return studentIndeksRepository.findByStudent_Korisnik_IdAndAktivanTrue(korisnikId)
                .orElseThrow(() -> new ForbiddenException("Nemate aktivan indeks"));
    }

    private void proveriDaJePredajaDozvoljena(Termin termin){
        LocalDateTime sada = LocalDateTime.now();

        if (sada.isBefore(termin.getPocetakIzrade())){
            throw new ConflictException("Termin jos nije poceo");
        }
        if (!sada.isBefore(termin.getKrajIzrade().plus(tolerancija))){
            throw new ConflictException("Vreme za predaju je isteklo");
        }
    }
}

