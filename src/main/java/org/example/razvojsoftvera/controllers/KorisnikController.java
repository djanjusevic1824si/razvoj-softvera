package org.example.razvojsoftvera.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.requests.KorisnikRequest;
import org.example.razvojsoftvera.responses.KorisnikResponse;
import org.example.razvojsoftvera.services.KorisnikService;
import org.example.razvojsoftvera.utils.Converters;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/korisnik")
public class KorisnikController {

    private final KorisnikService korisnikService;

    @PostMapping(path = "/add")
    public Long addKorisnik(@RequestBody @Valid KorisnikRequest request) {
        return korisnikService.save(request.email(), request.lozinka(), request.uloga()).getId();
    }

    @GetMapping(path = "/all")
    public List<KorisnikResponse> getAllKorisnik() {
        return Converters.toList(korisnikService.findAll(), Converters::toKorisnikResponse);
    }

    @GetMapping(path = "/{id}")
    public KorisnikResponse getKorisnikById(@PathVariable Long id) {
        return Converters.toKorisnikResponse(korisnikService.findById(id));
    }
}
