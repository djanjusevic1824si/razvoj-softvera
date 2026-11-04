package org.example.razvojsoftvera.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.requests.PrijavaCreateRequest;
import org.example.razvojsoftvera.requests.PrijavaUpdateRequest;
import org.example.razvojsoftvera.responses.PrijavaResponse;
import org.example.razvojsoftvera.services.PrijavaService;
import org.example.razvojsoftvera.utils.Converters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/prijave")
public class PrijavaController {

    private final PrijavaService prijavaService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public PrijavaResponse add(@Valid @RequestBody PrijavaCreateRequest request) {
        return Converters.toPrijavaResponse(prijavaService.save(request));
    }

    @GetMapping("/all")
    public List<PrijavaResponse> findAll() {
        return Converters.toList(prijavaService.findAll(), Converters::toPrijavaResponse);
    }

    @GetMapping("/svi")
    public Page<PrijavaResponse> findAll(Pageable pageable) {
        return prijavaService.findAll(pageable).map(Converters::toPrijavaResponse);
    }

    @GetMapping("/{id}")
    public PrijavaResponse findById(@PathVariable Long id) {
        return Converters.toPrijavaResponse(prijavaService.findById(id));
    }

    @PutMapping("/{id}")
    public PrijavaResponse update(@PathVariable Long id, @Valid @RequestBody PrijavaUpdateRequest request) {
        return Converters.toPrijavaResponse(prijavaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        prijavaService.deleteById(id);
    }

    @GetMapping("/ispit/{ispitId}")
    public Page<PrijavaResponse> findByIspit(@PathVariable Long ispitId, Pageable pageable) {
        return prijavaService.findByIspit(ispitId, pageable).map(Converters::toPrijavaResponse);
    }

    @GetMapping("/student-indeks/{studentIndeksId}")
    public Page<PrijavaResponse> findByStudentIndeks(@PathVariable Long studentIndeksId, Pageable pageable) {
        return prijavaService.findByStudentIndeks(studentIndeksId, pageable).map(Converters::toPrijavaResponse);
    }

    @GetMapping("/ispit/{ispitId}/broj")
    public long countByIspit(@PathVariable Long ispitId) {
        return prijavaService.countByIspit(ispitId);
    }

    @GetMapping("/ispit/{ispitId}/student-indeks/{studentIndeksId}/postoji")
    public boolean jePrijavljen(@PathVariable Long ispitId, @PathVariable Long studentIndeksId) {
        return prijavaService.jePrijavljen(ispitId, studentIndeksId);
    }
}
