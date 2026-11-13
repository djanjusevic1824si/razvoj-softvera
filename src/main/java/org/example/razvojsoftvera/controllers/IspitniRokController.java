package org.example.razvojsoftvera.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.requests.IspitniRokCreateRequest;
import org.example.razvojsoftvera.requests.IspitniRokUpdateRequest;
import org.example.razvojsoftvera.responses.IspitniRokResponse;
import org.example.razvojsoftvera.services.IspitniRokService;
import org.example.razvojsoftvera.utils.Converters;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ispitni-rokovi")
public class IspitniRokController {

    private final IspitniRokService ispitniRokService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public IspitniRokResponse add(@Valid @RequestBody IspitniRokCreateRequest request) {
        return Converters.toIspitniRokResponse(ispitniRokService.save(request));
    }

    @GetMapping("/all")
    public List<IspitniRokResponse> findAll() {
        return Converters.toList(ispitniRokService.findAll(), Converters::toIspitniRokResponse);
    }

    @GetMapping("/svi")
    public Page<IspitniRokResponse> findAll(@ParameterObject Pageable pageable) {
        return ispitniRokService.findAll(pageable).map(Converters::toIspitniRokResponse);
    }

    @GetMapping("/{id}")
    public IspitniRokResponse findById(@PathVariable Long id) {
        return Converters.toIspitniRokResponse(ispitniRokService.findById(id));
    }

    @PutMapping("/{id}")
    public IspitniRokResponse update(@PathVariable Long id, @Valid @RequestBody IspitniRokUpdateRequest request) {
        return Converters.toIspitniRokResponse(ispitniRokService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        ispitniRokService.deleteById(id);
    }

    @GetMapping("/skolska-godina")
    public Page<IspitniRokResponse> findBySkolskaGodina(@RequestParam String skolskaGodina, @ParameterObject Pageable pageable) {
        return ispitniRokService.findBySkolskaGodina(skolskaGodina, pageable).map(Converters::toIspitniRokResponse);
    }

    @GetMapping("/search")
    public Page<IspitniRokResponse> search(@RequestParam String naziv, @ParameterObject Pageable pageable) {
        return ispitniRokService.search(naziv, pageable).map(Converters::toIspitniRokResponse);
    }

    @GetMapping("/aktivni")
    public List<IspitniRokResponse> findAktivni(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datum) {
        return Converters.toList(ispitniRokService.findAktivni(datum), Converters::toIspitniRokResponse);
    }
}
