package org.example.razvojsoftvera.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.requests.StudentRequest;
import org.example.razvojsoftvera.requests.StudentUpdateRequest;
import org.example.razvojsoftvera.responses.StudentListResponse;
import org.example.razvojsoftvera.responses.StudentResponse;
import org.example.razvojsoftvera.services.StudentService;
import org.example.razvojsoftvera.utils.Converters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studenti")
public class StudentController {

    private final StudentService studentService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public StudentResponse add(@Valid @RequestBody StudentRequest request) {
        return Converters.toStudentResponse(studentService.save(request));
    }

    @GetMapping("/all")
    public List<StudentListResponse> findAll() {
        return Converters.toList(
                studentService.findAll(),
                Converters::toStudentListResponse
        );
    }

    @GetMapping("/svi")
    public Page<StudentListResponse> findAll(Pageable pageable) {
        return studentService.findAll(pageable)
                .map(Converters::toStudentListResponse);
    }

    @GetMapping("/{id}")
    public StudentResponse findById(@PathVariable Long id) {
        return Converters.toStudentResponse(studentService.findById(id));
    }

    @PutMapping("/{id}")
    public StudentResponse update(
            @PathVariable Long id,
            @Valid @RequestBody StudentUpdateRequest request
    ) {
        return Converters.toStudentResponse(studentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        studentService.deleteById(id);
    }

    @GetMapping("/search")
    public List<StudentResponse> search(
            @RequestParam(required = false) String ime,
            @RequestParam(required = false) String prezime
    ) {
        return Converters.toList(
                studentService.findByImeAndPrezime(ime, prezime),
                Converters::toStudentResponse
        );
    }

    @GetMapping("/search/svi")
    public Page<StudentResponse> search(
            @RequestParam(required = false) String ime,
            @RequestParam(required = false) String prezime,
            Pageable pageable
    ) {
        return studentService.findByImeAndPrezime(ime, prezime, pageable)
                .map(Converters::toStudentResponse);
    }

    @GetMapping("/by-korisnik/{korisnikId}")
    public StudentResponse findByKorisnikId(@PathVariable Long korisnikId) {
        return Converters.toStudentResponse(
                studentService.findByKorisnikId(korisnikId)
        );
    }
}