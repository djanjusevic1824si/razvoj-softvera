package org.example.razvojsoftvera.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.requests.*;
import org.example.razvojsoftvera.responses.StudentIndeksResponse;
import org.example.razvojsoftvera.services.StudentIndeksService;
import org.example.razvojsoftvera.utils.Converters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/student-indeksi")
public class StudentIndeksController {

    private final StudentIndeksService studentIndeksService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public StudentIndeksResponse save(
            @Valid @RequestBody StudentIndeksCreateRequest request
    ) {
        return Converters.toStudentIndeksResponse(
                studentIndeksService.save(request)
        );
    }

    @PutMapping("/{id}")
    public StudentIndeksResponse update(
            @PathVariable Long id,
            @Valid @RequestBody StudentIndeksUpdateRequest request
    ) {
        return Converters.toStudentIndeksResponse(
                studentIndeksService.update(id, request)
        );
    }

    @GetMapping("/all")
    public List<StudentIndeksResponse> findAll() {
        return Converters.toList(
                studentIndeksService.findAll(),
                Converters::toStudentIndeksResponse
        );
    }

    @GetMapping("/svi")
    public Page<StudentIndeksResponse> findAll(Pageable pageable) {
        return studentIndeksService.findAll(pageable)
                .map(Converters::toStudentIndeksResponse);
    }

    @GetMapping("/{id}")
    public StudentIndeksResponse findById(@PathVariable Long id) {
        return Converters.toStudentIndeksResponse(
                studentIndeksService.findById(id)
        );
    }

    @GetMapping("/student/{studentId}")
    public Page<StudentIndeksResponse> findByStudent(
            @PathVariable Long studentId,
            Pageable pageable
    ) {
        return studentIndeksService.findByStudent(studentId, pageable)
                .map(Converters::toStudentIndeksResponse);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        studentIndeksService.deleteById(id);
    }
}