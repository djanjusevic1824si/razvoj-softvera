package org.example.razvojsoftvera.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.model.Uloga;
import org.example.razvojsoftvera.requests.KorisnikPasswordRequest;
import org.example.razvojsoftvera.requests.KorisnikRequest;
import org.example.razvojsoftvera.requests.KorisnikUpdateRequest;
import org.example.razvojsoftvera.responses.KorisnikResponse;
import org.example.razvojsoftvera.services.KorisnikService;
import org.example.razvojsoftvera.utils.Converters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/korisnici")
public class KorisnikController {

    private final KorisnikService korisnikService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public KorisnikResponse add(@Valid @RequestBody KorisnikRequest request) {
        return Converters.toKorisnikResponse(korisnikService.save(request));
    }

    @GetMapping("/all")
    public List<KorisnikResponse> findAll() {
        return Converters.toList(
                korisnikService.findAll(),
                Converters::toKorisnikResponse
        );
    }

    @GetMapping("/svi")
    public Page<KorisnikResponse> findAll(Pageable pageable) {
        return korisnikService.findAll(pageable)
                .map(Converters::toKorisnikResponse);
    }

    @GetMapping("/{id}")
    public KorisnikResponse findById(@PathVariable Long id) {
        return Converters.toKorisnikResponse(korisnikService.findById(id));
    }

    @PutMapping("/{id}")
    public KorisnikResponse update(
            @PathVariable Long id,
            @Valid @RequestBody KorisnikUpdateRequest request
    ) {
        return Converters.toKorisnikResponse(
                korisnikService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        korisnikService.deleteById(id);
    }

    @GetMapping("/by-uloga")
    public Page<KorisnikResponse> findByUloga(
            @RequestParam Uloga uloga,
            Pageable pageable
    ) {
        return korisnikService.findByUloga(uloga, pageable)
                .map(Converters::toKorisnikResponse);
    }

    @PutMapping("/{id}/lozinka")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(
            @PathVariable Long id,
            @Valid @RequestBody KorisnikPasswordRequest request
    ) {
        korisnikService.resetPassword(id, request);
    }
}