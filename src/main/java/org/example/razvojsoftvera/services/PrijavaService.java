package org.example.razvojsoftvera.services;

import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.exceptions.BadRequestException;
import org.example.razvojsoftvera.exceptions.ConflictException;
import org.example.razvojsoftvera.exceptions.ResourceNotFoundException;
import org.example.razvojsoftvera.model.Ispit;
import org.example.razvojsoftvera.model.Prijava;
import org.example.razvojsoftvera.model.StudentIndeks;
import org.example.razvojsoftvera.repositories.*;
import org.example.razvojsoftvera.requests.PrijavaCreateRequest;
import org.example.razvojsoftvera.requests.PrijavaUpdateRequest;
import org.example.razvojsoftvera.requests.SpisakPrijavaRequest;
import org.example.razvojsoftvera.requests.StavkaSpiska;
import org.example.razvojsoftvera.responses.SpisakPrijavaResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrijavaService {

    private final PrijavaRepository prijavaRepository;
    private final StudentIndeksRepository studentIndeksRepository;
    private final IspitRepository ispitRepository;
    private final RadRepository radRepository;
    private final DrziPredmetRepository drziPredmetRepository;

    @Transactional
    public Prijava save(PrijavaCreateRequest request) {
        StudentIndeks indeks = studentIndeksRepository
                .findById(request.studentIndeksId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Studentski indeks sa id "
                                + request.studentIndeksId()
                                + " ne postoji"
                ));

        Ispit ispit = ispitRepository.findById(request.ispitId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ispit sa id " + request.ispitId() + " ne postoji"
                ));

        if (!indeks.isAktivan()) {
            throw new BadRequestException(
                    "Studentski indeks nije aktivan"
            );
        }

        if (!drziPredmetRepository.existsByPredmet_IdAndStudijskiProgram_Id(
                ispit.getPredmet().getId(), indeks.getStudijskiProgram().getId())) {
            throw new BadRequestException("Predmet se ne drzi na programu ovog indeksa");
        }

        boolean postoji = prijavaRepository.existsByStudentIndeks_IdAndIspit_Id(
                indeks.getId(),
                ispit.getId()
        );

        if (postoji) {
            throw new ConflictException(
                    "Student je vec prijavljen za ovaj ispit"
            );
        }

        Prijava prijava = new Prijava();
        prijava.setStudentIndeks(indeks);
        prijava.setIspit(ispit);

        try {
            return prijavaRepository.saveAndFlush(prijava);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Prijavu nije moguce sacuvati zbog duplikata ili konflikta povezanih podataka"
            );
        }
    }

    @Transactional
    public Prijava update(Long id, PrijavaUpdateRequest request) {
        Prijava prijava = findByIdOrThrow(id);

        StudentIndeks indeks = studentIndeksRepository
                .findById(request.studentIndeksId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Studentski indeks sa id "
                                + request.studentIndeksId()
                                + " ne postoji"
                ));

        Ispit ispit = ispitRepository.findById(request.ispitId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ispit sa id " + request.ispitId() + " ne postoji"
                ));

        if (!indeks.isAktivan()) {
            throw new BadRequestException(
                    "Studentski indeks nije aktivan"
            );
        }

        boolean postoji =
                prijavaRepository.existsByStudentIndeks_IdAndIspit_IdAndIdNot(
                        indeks.getId(),
                        ispit.getId(),
                        id
                );

        if (postoji) {
            throw new ConflictException(
                    "Student je vec prijavljen za ovaj ispit"
            );
        }

        boolean menjaIspit = !prijava.getIspit().getId()
                .equals(ispit.getId());

        boolean menjaIndeks = !prijava.getStudentIndeks().getId()
                .equals(indeks.getId());

        if ((menjaIspit || menjaIndeks)
                && radRepository.existsByPrijava_Id(id)) {
            throw new ConflictException(
                    "Prijavi sa radom ne moze da se promeni student ili ispit"
            );
        }

        prijava.setStudentIndeks(indeks);
        prijava.setIspit(ispit);

        try {
            return prijavaRepository.saveAndFlush(prijava);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Prijavu nije moguce izmeniti zbog duplikata ili konflikta povezanih podataka"
            );
        }
    }

    @Transactional
    @PreAuthorize("hasRole('NASTAVNIK')")
    public SpisakPrijavaResponse unesiSpisak(Long ispitId, SpisakPrijavaRequest request){
        Ispit ispit = ispitRepository.findById(ispitId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ispit sa ID-jem " + ispitId + " ne postoji"
                ));

        int dodato = 0;
        List<String> greske = new ArrayList<>();

        for(StavkaSpiska s: request.spisakIndeksa()){
            String oznaka = s.program() + " " + s.broj() + "/" + s.godinaUpisa();

            Optional<StudentIndeks> pronadjen = studentIndeksRepository.nadjiPoOznaci(
                            s.program(),
                            s.broj(),
                            s.godinaUpisa()
                    );
            if (pronadjen.isEmpty()){
                greske.add(oznaka + ": indeks ne postoji");
                continue;
            }

            StudentIndeks indeks = pronadjen.get();

            if (!indeks.isAktivan()){
                greske.add(oznaka + ": indeks nije aktivan");
                continue;
            }

            if(!drziPredmetRepository.existsByPredmet_IdAndStudijskiProgram_Id(
                    ispit.getPredmet().getId(), indeks.getStudijskiProgram().getId()
            )){
                greske.add(oznaka + ": predmet se ne drzi na programu ovog indeksa");
                continue;
            }

            if (prijavaRepository.existsByStudentIndeks_IdAndIspit_Id(
                    indeks.getId(), ispit.getId()
            )){
                greske.add(oznaka + ": indeks je vec prijavljen na ispit");
                continue;
            }

            Prijava prijava = new Prijava();
            prijava.setStudentIndeks(indeks);
            prijava.setIspit(ispit);
            prijavaRepository.save(prijava);
            dodato++;
        }

        return new SpisakPrijavaResponse(dodato, greske);
    }

    public List<Prijava> findAll() {
        return prijavaRepository.findAll();
    }

    public Page<Prijava> findAll(Pageable pageable) {
        return prijavaRepository.findAll(pageable);
    }

    @PreAuthorize("""
        hasRole('NASTAVNIK')
        or (
            hasRole('STUDENT')
            and @pristupService.jeSvojaPrijava(#p0, authentication)
        )
        """)
    public Prijava findById(Long id) {
        return findByIdOrThrow(id);
    }

    public Page<Prijava> findByIspit(
            Long ispitId,
            Pageable pageable
    ) {
        proveriIspit(ispitId);

        return prijavaRepository.findAllByIspit_Id(ispitId, pageable);
    }

    @PreAuthorize("""
        hasRole('NASTAVNIK')
        or (
            hasRole('STUDENT')
            and @pristupService.jeSvojIndeks(#p0, authentication)
        )
        """)
    public Page<Prijava> findByStudentIndeks(
            Long studentIndeksId,
            Pageable pageable
    ) {
        proveriIndeks(studentIndeksId);

        return prijavaRepository.findAllByStudentIndeks_Id(
                studentIndeksId,
                pageable
        );
    }

    public long countByIspit(Long ispitId) {
        proveriIspit(ispitId);
        return prijavaRepository.countByIspit_Id(ispitId);
    }

    public boolean jePrijavljen(Long ispitId, Long studentIndeksId) {
        proveriIspit(ispitId);
        proveriIndeks(studentIndeksId);

        return prijavaRepository.existsByStudentIndeks_IdAndIspit_Id(
                studentIndeksId,
                ispitId
        );
    }

    @Transactional
    public void deleteById(Long id) {
        Prijava prijava = findByIdOrThrow(id);

        try {
            prijavaRepository.delete(prijava);
            prijavaRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Prijava ne moze da se obrise jer za nju postoji rad"
            );
        }
    }

    private void proveriIspit(Long id) {
        if (!ispitRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Ispit sa id " + id + " ne postoji"
            );
        }
    }

    private void proveriIndeks(Long id) {
        if (!studentIndeksRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Studentski indeks sa id " + id + " ne postoji"
            );
        }
    }

    private Prijava findByIdOrThrow(Long id) {
        return prijavaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Prijava sa id " + id + " ne postoji"
                ));
    }
}