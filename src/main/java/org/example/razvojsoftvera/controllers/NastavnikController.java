package org.example.razvojsoftvera.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.requests.NastavnikRequest;
import org.example.razvojsoftvera.requests.NastavnikUpdateRequest;
import org.example.razvojsoftvera.responses.NastavnikResponse;
import org.example.razvojsoftvera.services.NastavnikService;
import org.example.razvojsoftvera.utils.Converters;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/nastavnici")
public class NastavnikController {

    private final NastavnikService nastavnikService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public NastavnikResponse add(@Valid @RequestBody NastavnikRequest request) {
        return Converters.toNastavnikResponse(nastavnikService.save(request));
    }

    @GetMapping("/all")
    public List<NastavnikResponse> findAll() {
        return Converters.toList(
                nastavnikService.findAll(),
                Converters::toNastavnikResponse
        );
    }

    @GetMapping("/svi")
    public Page<NastavnikResponse> findAll(@ParameterObject Pageable pageable) {
        return nastavnikService.findAll(pageable)
                .map(Converters::toNastavnikResponse);
    }

    @GetMapping("/{id}")
    public NastavnikResponse findById(@PathVariable Long id) {
        return Converters.toNastavnikResponse(nastavnikService.findById(id));
    }

    @PutMapping("/{id}")
    public NastavnikResponse update(
            @PathVariable Long id,
            @Valid @RequestBody NastavnikUpdateRequest request
    ) {
        return Converters.toNastavnikResponse(
                nastavnikService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        nastavnikService.deleteById(id);
    }

    @GetMapping("/search")
    public List<NastavnikResponse> search(
            @RequestParam(required = false) String ime,
            @RequestParam(required = false) String prezime
    ) {
        return Converters.toList(
                nastavnikService.findByImeAndPrezime(ime, prezime),
                Converters::toNastavnikResponse
        );
    }

    @GetMapping("/search/svi")
    public Page<NastavnikResponse> search(
            @RequestParam(required = false) String ime,
            @RequestParam(required = false) String prezime,
            @ParameterObject Pageable pageable
    ) {
        return nastavnikService.findByImeAndPrezime(ime, prezime, pageable)
                .map(Converters::toNastavnikResponse);
    }

    @GetMapping("/by-korisnik/{korisnikId}")
    public NastavnikResponse findByKorisnikId(@PathVariable Long korisnikId) {
        return Converters.toNastavnikResponse(
                nastavnikService.findByKorisnikId(korisnikId)
        );
    }
}