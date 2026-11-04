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
public class DrziPredmetService {

    private final DrziPredmetRepository drziPredmetRepository;
    private final NastavnikRepository nastavnikRepository;
    private final PredmetRepository predmetRepository;
    private final StudijskiProgramRepository studijskiProgramRepository;

    @Transactional
    public DrziPredmet save(DrziPredmetCreateRequest request) {
        Nastavnik nastavnik = nastavnikRepository.findById(request.nastavnikId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nastavnik ne postoji"
                ));

        Predmet predmet = predmetRepository.findById(request.predmetId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Predmet ne postoji"
                ));

        StudijskiProgram program = studijskiProgramRepository
                .findById(request.studijskiProgramId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Studijski program ne postoji"
                ));

        boolean postoji = drziPredmetRepository
                .existsByNastavnik_IdAndPredmet_IdAndStudijskiProgram_Id(
                        nastavnik.getId(),
                        predmet.getId(),
                        program.getId()
                );

        if (postoji) {
            throw new ConflictException(
                    "Nastavnik vec drzi ovaj predmet na izabranom programu"
            );
        }

        DrziPredmet drziPredmet = new DrziPredmet();
        drziPredmet.setNastavnik(nastavnik);
        drziPredmet.setPredmet(predmet);
        drziPredmet.setStudijskiProgram(program);

        try {
            return drziPredmetRepository.saveAndFlush(drziPredmet);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Angazovanje nije moguce sacuvati zbog konflikta podataka"
            );
        }
    }

    @Transactional
    public DrziPredmet update(
            Long id,
            DrziPredmetUpdateRequest request
    ) {
        DrziPredmet drziPredmet = findByIdOrThrow(id);

        Nastavnik nastavnik = nastavnikRepository.findById(request.nastavnikId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nastavnik ne postoji"
                ));

        Predmet predmet = predmetRepository.findById(request.predmetId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Predmet ne postoji"
                ));

        StudijskiProgram program = studijskiProgramRepository
                .findById(request.studijskiProgramId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Studijski program ne postoji"
                ));

        boolean postoji = drziPredmetRepository
                .existsByNastavnik_IdAndPredmet_IdAndStudijskiProgram_IdAndIdNot(
                        nastavnik.getId(),
                        predmet.getId(),
                        program.getId(),
                        id
                );

        if (postoji) {
            throw new ConflictException(
                    "Nastavnik vec drzi ovaj predmet na izabranom programu"
            );
        }

        drziPredmet.setNastavnik(nastavnik);
        drziPredmet.setPredmet(predmet);
        drziPredmet.setStudijskiProgram(program);

        try {
            return drziPredmetRepository.saveAndFlush(drziPredmet);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Angazovanje nije moguce izmeniti zbog konflikta podataka"
            );
        }
    }

    public List<DrziPredmet> findAll() {
        return drziPredmetRepository.findAll();
    }

    public Page<DrziPredmet> findAll(Pageable pageable) {
        return drziPredmetRepository.findAll(pageable);
    }

    public DrziPredmet findById(Long id) {
        return findByIdOrThrow(id);
    }

    public Page<DrziPredmet> findByNastavnik(
            Long nastavnikId,
            Pageable pageable
    ) {
        if (!nastavnikRepository.existsById(nastavnikId)) {
            throw new ResourceNotFoundException("Nastavnik ne postoji");
        }

        return drziPredmetRepository.findAllByNastavnik_Id(
                nastavnikId,
                pageable
        );
    }

    @Transactional
    public void deleteById(Long id) {
        DrziPredmet drziPredmet = findByIdOrThrow(id);

        try {
            drziPredmetRepository.delete(drziPredmet);
            drziPredmetRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Angazovanje nije moguce obrisati zbog povezanih podataka"
            );
        }
    }

    private DrziPredmet findByIdOrThrow(long id) {
        return drziPredmetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Angazovanje sa id " + id + " ne postoji"
                ));
    }
}