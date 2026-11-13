package org.example.razvojsoftvera.services;

import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.exceptions.BadRequestException;
import org.example.razvojsoftvera.exceptions.ConflictException;
import org.example.razvojsoftvera.exceptions.ResourceNotFoundException;
import org.example.razvojsoftvera.model.*;
import org.example.razvojsoftvera.repositories.*;
import org.example.razvojsoftvera.requests.RadCreateRequest;
import org.example.razvojsoftvera.requests.RadOcenaRequest;
import org.example.razvojsoftvera.requests.RadUpdateRequest;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RadService {

    private final RadRepository radRepository;
    private final PrijavaRepository prijavaRepository;
    private final GrupaRepository grupaRepository;
    private final NastavnikRepository nastavnikRepository;
    private final IspitRepository ispitRepository;
    private final PredajaRepository predajaRepository;
    private final SkladisteFajlova skladiste;
    private final RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    @Transactional
    public Rad save(RadCreateRequest request) {
        Prijava prijava = prijavaRepository.findById(request.prijavaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Prijava ne postoji"
                ));

        Grupa grupa = grupaRepository.findById(request.grupaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Grupa ne postoji"
                ));

        if (!prijava.getStudentIndeks().isAktivan()) {
            throw new BadRequestException(
                    "Studentski indeks nije aktivan"
            );
        }

        boolean odgovaraIspit = prijava.getIspit().getId()
                .equals(grupa.getTermin().getIspit().getId());

        if (!odgovaraIspit) {
            throw new BadRequestException(
                    "Grupa ne pripada ispitu za koji je student prijavljen"
            );
        }

        if (radRepository.existsByPrijava_Id(prijava.getId())) {
            throw new ConflictException(
                    "Za ovu prijavu vec postoji rad"
            );
        }

        Rad rad = new Rad();
        rad.setPrijava(prijava);
        rad.setGrupa(grupa);
        rad.setUcionica(request.ucionica());
        rad.setStatusRada(StatusRada.PREUZET);

        try {
            return radRepository.saveAndFlush(rad);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Rad nije moguce sacuvati zbog duplikata ili povezanih podataka"
            );
        }
    }

    @Transactional
    public Rad update(Long id, RadUpdateRequest request) {
        Rad rad = findByIdForUpdateOrThrow(id);
        rad.setUcionica(request.ucionica());

        try {
            return radRepository.saveAndFlush(rad);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Rad nije moguce izmeniti zbog konflikta podataka"
            );
        }
    }

    public List<Rad> findAll() {
        return radRepository.findAll();
    }

    public Page<Rad> findAll(Pageable pageable) {
        return radRepository.findAll(pageable);
    }

    public Rad findById(Long id) {
        return radRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Rad sa id " + id + " ne postoji"
                ));
    }

    public Page<Rad> findByIspit(Long ispitId, Pageable pageable) {
        if (!ispitRepository.existsById(ispitId)) {
            throw new ResourceNotFoundException("Ispit ne postoji");
        }

        return radRepository.findAllByPrijava_Ispit_Id(
                ispitId,
                pageable
        );
    }

    @Transactional
    public void deleteById(Long id) {
        Rad rad = findByIdForUpdateOrThrow(id);

        if (rad.getStatusRada() == StatusRada.PREDAT) {
            throw new ConflictException(
                    "Konacno predat rad nije moguce obrisati"
            );
        }

        try {
            radRepository.delete(rad);
            radRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Rad nije moguce obrisati zbog povezanih podataka"
            );
        }
    }

    private Rad findByIdForUpdateOrThrow(long id) {
        return radRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Rad sa id " + id + " ne postoji"
                ));
    }

    @PreAuthorize("hasRole('NASTAVNIK')")
    public Resource preuzmiResenje(Long radId){
        Rad rad = radRepository.findById(radId)
                .orElseThrow(() -> new ResourceNotFoundException("Rad sa id-jem: " + radId + " ne postoji"));

        if (rad.getStatusRada() != StatusRada.PREDAT){
            throw new ConflictException("Rad jos uvek nije predat");
        }

        Predaja konacna = predajaRepository.findByRad_IdAndTipPredaje(radId, TipPredaje.KONACNA)
                .orElseThrow(() -> new ResourceNotFoundException("Rad nema konacnu predaju"));

        return skladiste.ucitaj(konacna.getPutanjaResenja());
    }
}