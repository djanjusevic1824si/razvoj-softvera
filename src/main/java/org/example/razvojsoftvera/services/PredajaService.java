package org.example.razvojsoftvera.services;

import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.exceptions.ConflictException;
import org.example.razvojsoftvera.exceptions.ResourceNotFoundException;
import org.example.razvojsoftvera.model.Predaja;
import org.example.razvojsoftvera.model.Rad;
import org.example.razvojsoftvera.model.StatusRada;
import org.example.razvojsoftvera.model.TipPredaje;
import org.example.razvojsoftvera.repositories.PredajaRepository;
import org.example.razvojsoftvera.repositories.RadRepository;
import org.example.razvojsoftvera.requests.PredajaCreateRequest;
import org.example.razvojsoftvera.requests.PredajaUpdateRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PredajaService {

    private final PredajaRepository predajaRepository;
    private final RadRepository radRepository;

    @Transactional
    public Predaja save(PredajaCreateRequest request) {
        Rad rad = findRadForUpdateOrThrow(request.radId());

        boolean postojiKonacna = predajaRepository
                .existsByRad_IdAndTipPredaje(
                        rad.getId(),
                        TipPredaje.KONACNA
                );

        if (postojiKonacna || rad.getStatusRada() == StatusRada.PREDAT) {
            throw new ConflictException(
                    "Rad je vec konacno predat"
            );
        }

        int redniBroj = predajaRepository
                .findMaxRedniBrojByRadId(rad.getId()) + 1;

        Predaja predaja = new Predaja();
        predaja.setRad(rad);
        predaja.setRedniBroj(redniBroj);
        predaja.setTipPredaje(request.tipPredaje());
        predaja.setPutanjaResenja(request.putanjaResenja().trim());

        if (request.tipPredaje() == TipPredaje.KONACNA) {
            rad.setStatusRada(StatusRada.PREDAT);
        }

        try {
            return predajaRepository.saveAndFlush(predaja);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Predaju nije moguce sacuvati zbog konflikta podataka"
            );
        }
    }

    @Transactional // mozda ne dozvoliti
    public Predaja update(Long id, PredajaUpdateRequest request) {
        Predaja predaja = findByIdOrThrow(id);
        findRadForUpdateOrThrow(predaja.getRad().getId());

        boolean postojiKonacna = predajaRepository
                .existsByRad_IdAndTipPredaje(
                        predaja.getRad().getId(),
                        TipPredaje.KONACNA
                );

        if (postojiKonacna) {
            throw new ConflictException(
                    "Predaje nije moguce menjati nakon konacne predaje rada"
            );
        }

        predaja.setPutanjaResenja(request.putanjaResenja().trim());

        try {
            return predajaRepository.saveAndFlush(predaja);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Predaju nije moguce izmeniti zbog konflikta podataka"
            );
        }
    }

    public List<Predaja> findAll() {
        return predajaRepository.findAll();
    }

    public Page<Predaja> findAll(Pageable pageable) {
        return predajaRepository.findAll(pageable);
    }

    public Predaja findById(Long id) {
        return findByIdOrThrow(id);
    }

    public Page<Predaja> findByRad(Long radId, Pageable pageable) {
        if (!radRepository.existsById(radId)) {
            throw new ResourceNotFoundException("Rad ne postoji");
        }

        return predajaRepository.findAllByRad_Id(radId, pageable);
    }

    @Transactional
    public void deleteById(Long id) {
        Predaja predaja = findByIdOrThrow(id);
        findRadForUpdateOrThrow(predaja.getRad().getId());

        boolean postojiKonacna = predajaRepository
                .existsByRad_IdAndTipPredaje(
                        predaja.getRad().getId(),
                        TipPredaje.KONACNA
                );

        if (postojiKonacna) {
            throw new ConflictException(
                    "Predaje nije moguce brisati nakon konacne predaje rada"
            );
        }

        try {
            predajaRepository.delete(predaja);
            predajaRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Predaju nije moguce obrisati zbog povezanih podataka"
            );
        }
    }

    private Predaja findByIdOrThrow(long id) {
        return predajaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Predaja sa id " + id + " ne postoji"
                ));
    }

    private Rad findRadForUpdateOrThrow(long id) {
        return radRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Rad sa id " + id + " ne postoji"
                ));
    }
}