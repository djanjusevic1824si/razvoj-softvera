package org.example.razvojsoftvera.services;

import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.exceptions.BadRequestException;
import org.example.razvojsoftvera.exceptions.ConflictException;
import org.example.razvojsoftvera.exceptions.ResourceNotFoundException;
import org.example.razvojsoftvera.model.Korisnik;
import org.example.razvojsoftvera.model.Student;
import org.example.razvojsoftvera.model.Uloga;
import org.example.razvojsoftvera.repositories.KorisnikRepository;
import org.example.razvojsoftvera.repositories.StudentRepository;
import org.example.razvojsoftvera.requests.StudentRequest;
import org.example.razvojsoftvera.requests.StudentUpdateRequest;
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
public class StudentService {

    private final StudentRepository studentRepository;
    private final KorisnikRepository korisnikRepository;

    @Transactional
    public Student save(StudentRequest request) {
        String jmbg = request.jmbg();

        if (jmbg != null) {
            jmbg = jmbg.trim();

            if (jmbg.isEmpty()) {
                jmbg = null;
            }
        }

        if (jmbg != null && studentRepository.existsByJmbg(jmbg)) {
            throw new ConflictException(
                    "Student sa tim JMBG-om vec postoji"
            );
        }

        Korisnik korisnik = null;

        if (request.korisnikId() != null) {
            korisnik = korisnikRepository.findById(request.korisnikId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Korisnik sa id " + request.korisnikId() + " ne postoji"
                    ));

            if (korisnik.getUloga() != Uloga.STUDENT) {
                throw new BadRequestException(
                        "Korisnik mora imati ulogu STUDENT"
                );
            }

            if (studentRepository.existsByKorisnikId(request.korisnikId())) {
                throw new ConflictException(
                        "Korisnik je vec povezan sa studentom"
                );
            }
        }

        Student student = new Student();
        student.setIme(request.ime().trim());
        student.setPrezime(request.prezime().trim());
        student.setJmbg(jmbg);
        student.setKorisnik(korisnik);

        try {
            return studentRepository.saveAndFlush(student);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Studenta nije moguce sacuvati zbog duplikata ili konflikta povezanih podataka"
            );
        }
    }

    @Transactional
    public Student update(Long id, StudentUpdateRequest request) {
        Student student = findByIdOrThrow(id);

        String jmbg = request.jmbg();

        if (jmbg != null) {
            jmbg = jmbg.trim();

            if (jmbg.isEmpty()) {
                jmbg = null;
            }
        }

        if (jmbg != null
                && studentRepository.existsByJmbgAndIdNot(jmbg, id)) {
            throw new ConflictException(
                    "Student sa tim JMBG-om vec postoji"
            );
        }

        student.setIme(request.ime().trim());
        student.setPrezime(request.prezime().trim());
        student.setJmbg(jmbg);

        try {
            return studentRepository.saveAndFlush(student);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Studenta nije moguce izmeniti zbog duplikata ili konflikta povezanih podataka"
            );
        }
    }

    @PreAuthorize("hasAnyRole('NASTAVNIK', 'STUDENT')")
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('NASTAVNIK', 'STUDENT')")
    public Page<Student> findAll(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    public Student findById(Long id) {
        return findByIdOrThrow(id);
    }

    public Student findByKorisnikId(Long korisnikId) {
        return studentRepository.findByKorisnikId(korisnikId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student za korisnika sa id " + korisnikId + " ne postoji"
                ));
    }

    public List<Student> findByImeAndPrezime(String ime, String prezime) {
        String imePretraga = ime == null ? "" : ime.trim();
        String prezimePretraga = prezime == null ? "" : prezime.trim();

        return studentRepository
                .findByImeContainingIgnoreCaseAndPrezimeContainingIgnoreCase(
                        imePretraga,
                        prezimePretraga
                );
    }

    public Page<Student> findByImeAndPrezime(
            String ime,
            String prezime,
            Pageable pageable
    ) {
        String imePretraga = ime == null ? "" : ime.trim();
        String prezimePretraga = prezime == null ? "" : prezime.trim();

        return studentRepository
                .findByImeContainingIgnoreCaseAndPrezimeContainingIgnoreCase(
                        imePretraga,
                        prezimePretraga,
                        pageable
                );
    }

    @Transactional
    public void deleteById(Long id) {
        Student student = findByIdOrThrow(id);

        try {
            studentRepository.delete(student);
            studentRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Student ne moze da se obrise jer postoje povezani podaci"
            );
        }
    }

    private Student findByIdOrThrow(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student sa id " + id + " ne postoji"
                ));
    }
}