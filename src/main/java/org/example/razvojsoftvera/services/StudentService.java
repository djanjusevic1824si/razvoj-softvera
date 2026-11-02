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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;
    private final KorisnikRepository korisnikRepository;

    @Transactional
    public Student save(Student student, Long korisnikId) {
        if (student.getJmbg() != null && studentRepository.existsByJmbg(student.getJmbg())) {
            throw new ConflictException(
                    "Student sa tim JMBG-om vec postoji"
            );
        }

        if (korisnikId != null) {
            Korisnik korisnik = korisnikRepository.findById(korisnikId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Korisnik sa id " + korisnikId + " ne postoji"
                    ));

            if (korisnik.getUloga() != Uloga.STUDENT) {
                throw new BadRequestException(
                        "Korisnik mora imati ulogu STUDENT"
                );
            }
            if (studentRepository.existsByKorisnikId(korisnikId)) {
                throw new ConflictException(
                        "Korisnik je vec povezan sa studentom"
                );
            }

            student.setKorisnik(korisnik);
        }

        try {
            return studentRepository.saveAndFlush(student);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Student nije sacuvan zbog konflikta podataka"
            );
        }
    }

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public Page<Student> findAll(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    public Student findById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student sa id " + id + " ne postoji"
                ));
    }

    public List<Student> findByImeAndPrezime(String ime, String prezime) {
        return studentRepository.findByImeAndPrezime(ime, prezime);
    }
}
