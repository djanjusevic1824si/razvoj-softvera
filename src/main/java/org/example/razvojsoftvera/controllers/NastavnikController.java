package org.example.razvojsoftvera.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.requests.NastavnikRequest;
import org.example.razvojsoftvera.responses.NastavnikResponse;
import org.example.razvojsoftvera.services.NastavnikService;
import org.example.razvojsoftvera.utils.Converters;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/nastavnik")
public class NastavnikController {

    private final NastavnikService nastavnikService;

    @PostMapping(path = "/add")
    public Long addNastavnik(@RequestBody @Valid NastavnikRequest request) {
        return nastavnikService.save(Converters.toNastavnik(request), request.korisnikId()).getId();
    }

    @GetMapping(path = "/all")
    public List<NastavnikResponse> getAllNastavnik() {
        return Converters.toList(nastavnikService.findAll(), Converters::toNastavnikResponse);
    }

    @GetMapping(path = "/{id}")
    public NastavnikResponse getNastavnikById(@PathVariable Long id) {
        return Converters.toNastavnikResponse(nastavnikService.findById(id));
    }

    @GetMapping(path = "/search")
    public List<NastavnikResponse> search(
            @RequestParam(required = false) String ime,
            @RequestParam(required = false) String prezime
    ) {
        return Converters.toList(
                nastavnikService.findByImeAndPrezime(ime, prezime),
                Converters::toNastavnikResponse
        );
    }
}
