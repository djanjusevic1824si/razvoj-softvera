package org.example.razvojsoftvera.services;

import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.exceptions.BadRequestException;
import org.example.razvojsoftvera.exceptions.ConflictException;
import org.example.razvojsoftvera.exceptions.ForbiddenException;
import org.example.razvojsoftvera.exceptions.ResourceNotFoundException;
import org.example.razvojsoftvera.model.*;
import org.example.razvojsoftvera.repositories.*;
import org.example.razvojsoftvera.requests.PolaganjeStartRequest;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        return skladiste.ucitaj(rad.getGrupa().getZadatak().getPutanjaZadatka());
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
}

