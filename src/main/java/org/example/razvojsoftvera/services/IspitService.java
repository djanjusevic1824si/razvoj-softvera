package org.example.razvojsoftvera.services;

import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.exceptions.ConflictException;
import org.example.razvojsoftvera.exceptions.ResourceNotFoundException;
import org.example.razvojsoftvera.model.Ispit;
import org.example.razvojsoftvera.model.IspitniRok;
import org.example.razvojsoftvera.model.Predmet;
import org.example.razvojsoftvera.repositories.*;
import org.example.razvojsoftvera.requests.IspitCreateRequest;
import org.example.razvojsoftvera.requests.IspitUpdateRequest;
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
public class IspitService {

    private final IspitRepository ispitRepository;
    private final PredmetRepository predmetRepository;
    private final IspitniRokRepository ispitniRokRepository;
    private final TerminRepository terminRepository;
    private final PrijavaRepository prijavaRepository;

    @Transactional
    public Ispit save(IspitCreateRequest request) {
        Predmet predmet = predmetRepository.findById(request.predmetId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Predmet ne postoji"
                ));

        IspitniRok rok = ispitniRokRepository.findById(request.ispitniRokId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ispitni rok ne postoji"
                ));

        boolean postoji = ispitRepository.existsByPredmet_IdAndIspitniRok_Id(
                predmet.getId(),
                rok.getId()
        );

        if (postoji) {
            throw new ConflictException(
                    "Ispit za ovaj predmet vec postoji u izabranom roku"
            );
        }

        Ispit ispit = new Ispit();
        ispit.setPredmet(predmet);
        ispit.setIspitniRok(rok);

        try {
            return ispitRepository.saveAndFlush(ispit);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Ispit nije moguce sacuvati zbog konflikta podataka"
            );
        }
    }

    @Transactional
    public Ispit update(Long id, IspitUpdateRequest request) {
        Ispit ispit = findByIdOrThrow(id);

        Predmet predmet = predmetRepository.findById(request.predmetId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Predmet ne postoji"
                ));

        IspitniRok rok = ispitniRokRepository.findById(request.ispitniRokId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ispitni rok ne postoji"
                ));

        boolean postoji =
                ispitRepository.existsByPredmet_IdAndIspitniRok_IdAndIdNot(
                        predmet.getId(),
                        rok.getId(),
                        id
                );

        if (postoji) {
            throw new ConflictException(
                    "Ispit za ovaj predmet vec postoji u izabranom roku"
            );
        }

        boolean menjaVeze =
                !ispit.getPredmet().getId().equals(predmet.getId())
                        || !ispit.getIspitniRok().getId().equals(rok.getId());

        if (menjaVeze && (terminRepository.existsByIspit_Id(id)
                || prijavaRepository.existsByIspit_Id(id))) {
            throw new ConflictException(
                    "Ispitu sa terminima ili prijavama nije moguce promeniti predmet ili rok"
            );
        }

        ispit.setPredmet(predmet);
        ispit.setIspitniRok(rok);

        try {
            return ispitRepository.saveAndFlush(ispit);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Ispit nije moguce izmeniti zbog konflikta podataka"
            );
        }
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'NASTAVNIK')")
    public List<Ispit> findAll() {
        return ispitRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'NASTAVNIK')")
    public Page<Ispit> findAll(Pageable pageable) {
        return ispitRepository.findAll(pageable);
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'NASTAVNIK')")
    public Ispit findById(Long id) {
        return findByIdOrThrow(id);
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'NASTAVNIK')")
    public Page<Ispit> findByIspitniRok(
            Long ispitniRokId,
            Pageable pageable
    ) {
        if (!ispitniRokRepository.existsById(ispitniRokId)) {
            throw new ResourceNotFoundException("Ispitni rok ne postoji");
        }

        return ispitRepository.findAllByIspitniRok_Id(
                ispitniRokId,
                pageable
        );
    }

    @Transactional
    public void deleteById(Long id) {
        Ispit ispit = findByIdOrThrow(id);

        try {
            ispitRepository.delete(ispit);
            ispitRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Ispit nije moguce obrisati jer postoje povezani termini ili prijave"
            );
        }
    }

    private Ispit findByIdOrThrow(long id) {
        return ispitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ispit sa id " + id + " ne postoji"
                ));
    }
}