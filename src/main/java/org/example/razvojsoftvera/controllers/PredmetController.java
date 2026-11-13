package org.example.razvojsoftvera.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.requests.PredmetRequest;
import org.example.razvojsoftvera.responses.PredmetResponse;
import org.example.razvojsoftvera.services.PredmetService;
import org.example.razvojsoftvera.utils.Converters;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/predmeti")
public class PredmetController {

    private final PredmetService predmetService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public PredmetResponse add(@Valid @RequestBody PredmetRequest request) {
        return Converters.toPredmetResponse(predmetService.save(request));
    }

    @GetMapping("/all")
    public List<PredmetResponse> findAll() {
        return Converters.toList(
                predmetService.findAll(),
                Converters::toPredmetResponse
        );
    }

    @GetMapping("/svi")
    public Page<PredmetResponse> findAll(@ParameterObject Pageable pageable) {
        return predmetService.findAll(pageable)
                .map(Converters::toPredmetResponse);
    }

    @GetMapping("/{id}")
    public PredmetResponse findById(@PathVariable Long id) {
        return Converters.toPredmetResponse(predmetService.findById(id));
    }

    @PutMapping("/{id}")
    public PredmetResponse update(
            @PathVariable Long id,
            @Valid @RequestBody PredmetRequest request
    ) {
        return Converters.toPredmetResponse(predmetService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        predmetService.deleteById(id);
    }

    @GetMapping("/by-sifra")
    public List<PredmetResponse> findBySifra(@RequestParam String sifra) {
        return Converters.toList(
                predmetService.findBySifra(sifra),
                Converters::toPredmetResponse
        );
    }

    @GetMapping("/by-naziv")
    public List<PredmetResponse> findByNaziv(@RequestParam String naziv) {
        return Converters.toList(
                predmetService.findByNaziv(naziv),
                Converters::toPredmetResponse
        );
    }

    @GetMapping("/search")
    public Page<PredmetResponse> search(
            @RequestParam(required = false) String sifra,
            @RequestParam(required = false) String naziv,
            @ParameterObject Pageable pageable
    ) {
        return predmetService.search(sifra, naziv, pageable)
                .map(Converters::toPredmetResponse);
    }
}