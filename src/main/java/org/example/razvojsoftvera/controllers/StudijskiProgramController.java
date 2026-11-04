package org.example.razvojsoftvera.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.requests.StudijskiProgramRequest;
import org.example.razvojsoftvera.responses.StudijskiProgramResponse;
import org.example.razvojsoftvera.services.StudijskiProgramService;
import org.example.razvojsoftvera.utils.Converters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studijski-programi")
public class StudijskiProgramController {

    private final StudijskiProgramService studijskiProgramService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public StudijskiProgramResponse add(
            @Valid @RequestBody StudijskiProgramRequest request
    ) {
        return Converters.toStudijskiProgramResponse(
                studijskiProgramService.save(request)
        );
    }

    @GetMapping("/all")
    public List<StudijskiProgramResponse> findAll() {
        return Converters.toList(
                studijskiProgramService.findAll(),
                Converters::toStudijskiProgramResponse
        );
    }

    @GetMapping("/svi")
    public Page<StudijskiProgramResponse> findAll(Pageable pageable) {
        return studijskiProgramService.findAll(pageable)
                .map(Converters::toStudijskiProgramResponse);
    }

    @GetMapping("/{id}")
    public StudijskiProgramResponse findById(@PathVariable Long id) {
        return Converters.toStudijskiProgramResponse(
                studijskiProgramService.findById(id)
        );
    }

    @PutMapping("/{id}")
    public StudijskiProgramResponse update(
            @PathVariable Long id,
            @Valid @RequestBody StudijskiProgramRequest request
    ) {
        return Converters.toStudijskiProgramResponse(
                studijskiProgramService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        studijskiProgramService.deleteById(id);
    }

    @GetMapping("/all/oznaka")
    public List<String> findAllOznaka() {
        return studijskiProgramService.findAllOznaka();
    }

    @GetMapping("/search")
    public Page<StudijskiProgramResponse> search(
            @RequestParam(required = false) String oznaka,
            @RequestParam(required = false) String naziv,
            Pageable pageable
    ) {
        return studijskiProgramService.search(oznaka, naziv, pageable)
                .map(Converters::toStudijskiProgramResponse);
    }
}