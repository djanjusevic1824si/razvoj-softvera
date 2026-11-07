package org.example.razvojsoftvera.services;

import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.exceptions.BadRequestException;
import org.example.razvojsoftvera.exceptions.ConflictException;
import org.example.razvojsoftvera.exceptions.ResourceNotFoundException;
import org.example.razvojsoftvera.model.Student;
import org.example.razvojsoftvera.model.StudentIndeks;
import org.example.razvojsoftvera.model.StudijskiProgram;
import org.example.razvojsoftvera.repositories.PrijavaRepository;
import org.example.razvojsoftvera.repositories.StudentIndeksRepository;
import org.example.razvojsoftvera.repositories.StudentRepository;
import org.example.razvojsoftvera.repositories.StudijskiProgramRepository;
import org.example.razvojsoftvera.requests.StudentIndeksCreateRequest;
import org.example.razvojsoftvera.requests.StudentIndeksUpdateRequest;
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
public class StudentIndeksService {

    private final StudentIndeksRepository studentIndeksRepository;
    private final StudentRepository studentRepository;
    private final StudijskiProgramRepository studijskiProgramRepository;
    private final PrijavaRepository prijavaRepository;

    @Transactional
    public StudentIndeks save(StudentIndeksCreateRequest request) {
        if (request.broj() < 1 || request.godinaUpisa() < 1) {
            throw new BadRequestException(
                    "Broj indeksa i godina upisa moraju biti pozitivni"
            );
        }

        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student ne postoji"
                ));

        StudijskiProgram program = studijskiProgramRepository
                .findById(request.studijskiProgramId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Studijski program ne postoji"
                ));

        boolean postoji = studentIndeksRepository
                .existsByStudijskiProgram_IdAndBrojAndGodinaUpisa(
                        program.getId(),
                        request.broj(),
                        request.godinaUpisa()
                );

        if (postoji) {
            throw new ConflictException(
                    "Indeks sa tim podacima vec postoji"
            );
        }

        StudentIndeks indeks = new StudentIndeks();
        indeks.setBroj(request.broj());
        indeks.setGodinaUpisa(request.godinaUpisa());
        indeks.setAktivan(true);
        indeks.setStudent(student);
        indeks.setStudijskiProgram(program);

        try {
            return studentIndeksRepository.saveAndFlush(indeks);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Indeks nije moguce sacuvati zbog konflikta podataka"
            );
        }
    }

    @Transactional
    public StudentIndeks update(
            Long id,
            StudentIndeksUpdateRequest request
    ) {
        StudentIndeks indeks = findByIdOrThrow(id);

        if (request.broj() < 1 || request.godinaUpisa() < 1) {
            throw new BadRequestException(
                    "Broj indeksa i godina upisa moraju biti pozitivni"
            );
        }

        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student ne postoji"
                ));

        StudijskiProgram program = studijskiProgramRepository
                .findById(request.studijskiProgramId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Studijski program ne postoji"
                ));

        boolean postoji = studentIndeksRepository
                .existsByStudijskiProgram_IdAndBrojAndGodinaUpisaAndIdNot(
                        program.getId(),
                        request.broj(),
                        request.godinaUpisa(),
                        id
                );

        if (postoji) {
            throw new ConflictException(
                    "Indeks sa tim podacima vec postoji"
            );
        }

        boolean menjaIdentitet =
                !indeks.getBroj().equals(request.broj())
                        || !indeks.getGodinaUpisa().equals(request.godinaUpisa())
                        || !indeks.getStudent().getId().equals(student.getId())
                        || !indeks.getStudijskiProgram().getId().equals(program.getId());

        if (menjaIdentitet && prijavaRepository.existsByStudentIndeks_Id(id)) {
            throw new ConflictException(
                    "Nije moguce promeniti identitet indeksa koji vec ima prijave"
            );
        }

        indeks.setBroj(request.broj());
        indeks.setGodinaUpisa(request.godinaUpisa());
        indeks.setAktivan(request.aktivan());
        indeks.setStudent(student);
        indeks.setStudijskiProgram(program);

        try {
            return studentIndeksRepository.saveAndFlush(indeks);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Indeks nije moguce izmeniti zbog konflikta podataka"
            );
        }
    }

    public List<StudentIndeks> findAll() {
        return studentIndeksRepository.findAll();
    }

    public Page<StudentIndeks> findAll(Pageable pageable) {
        return studentIndeksRepository.findAll(pageable);
    }

    @PreAuthorize("""
        hasRole('NASTAVNIK')
        or (
            hasRole('STUDENT')
            and @pristupService.jeSvojIndeks(#p0, authentication)
        )
        """)
    public StudentIndeks findById(Long id) {
        return findByIdOrThrow(id);
    }

    @PreAuthorize("""
        hasRole('NASTAVNIK')
        or (
            hasRole('STUDENT')
            and @pristupService.jeSvojProfil(#p0, authentication)
        )
        """)
    public Page<StudentIndeks> findByStudent(
            Long studentId,
            Pageable pageable
    ) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException(
                    "Student sa id " + studentId + " ne postoji"
            );
        }

        return studentIndeksRepository.findAllByStudent_Id(
                studentId,
                pageable
        );
    }

    @Transactional
    public void deleteById(Long id) {
        StudentIndeks indeks = findByIdOrThrow(id);

        try {
            studentIndeksRepository.delete(indeks);
            studentIndeksRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "Indeks nije moguce obrisati jer postoje povezane prijave"
            );
        }
    }

    private StudentIndeks findByIdOrThrow(long id) {
        return studentIndeksRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Studentski indeks sa id " + id + " ne postoji"
                ));
    }
}