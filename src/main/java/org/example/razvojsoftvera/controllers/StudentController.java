package org.example.razvojsoftvera.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.requests.StudentRequest;
import org.example.razvojsoftvera.responses.StudentResponse;
import org.example.razvojsoftvera.services.StudentService;
import org.example.razvojsoftvera.utils.Converters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/student")
public class StudentController {

    private final StudentService studentService;

    @PostMapping(path = "/add")
    public Long addStudent(@RequestBody @Valid StudentRequest request) {
        return studentService.save(Converters.toStudent(request), request.korisnikId()).getId();
    }

    @GetMapping(path = "/all")
    public List<StudentResponse> getAllStudent() {
        return Converters.toList(studentService.findAll(), Converters::toStudentResponse);
    }

    @GetMapping(path = "/svi")
    public Page<StudentResponse> getAllStudentPaginated(Pageable pageable) {
        return studentService.findAll(pageable).map(Converters::toStudentResponse);
    }

    @GetMapping(path = "/{id}")
    public StudentResponse getStudentById(@PathVariable Long id) {
        return Converters.toStudentResponse(studentService.findById(id));
    }

    @GetMapping(path = "/search")
    public List<StudentResponse> search(
            @RequestParam(required = false) String ime,
            @RequestParam(required = false) String prezime
    ) {
        return Converters.toList(
                studentService.findByImeAndPrezime(ime, prezime),
                Converters::toStudentResponse
        );
    }
}
