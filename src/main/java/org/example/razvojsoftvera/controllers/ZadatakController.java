package org.example.razvojsoftvera.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.requests.ZadatakCreateRequest;
import org.example.razvojsoftvera.requests.ZadatakUpdateRequest;
import org.example.razvojsoftvera.responses.ZadatakResponse;
import org.example.razvojsoftvera.services.ZadatakService;
import org.example.razvojsoftvera.utils.Converters;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/zadaci")
public class ZadatakController {

    private final ZadatakService zadatakService;

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ZadatakResponse add(@Valid @ModelAttribute ZadatakCreateRequest request) {
        return Converters.toZadatakResponse(zadatakService.save(request));
    }

    @GetMapping("/all")
    public List<ZadatakResponse> findAll() {
        return Converters.toList(zadatakService.findAll(), Converters::toZadatakResponse);
    }

    @GetMapping("/svi")
    public Page<ZadatakResponse> findAll(Pageable pageable) {
        return zadatakService.findAll(pageable).map(Converters::toZadatakResponse);
    }

    @GetMapping("/{id}")
    public ZadatakResponse findById(@PathVariable Long id) {
        return Converters.toZadatakResponse(zadatakService.findById(id));
    }

    @PutMapping("/{id}")
    public ZadatakResponse update(@PathVariable Long id, @Valid @RequestBody ZadatakUpdateRequest request) {
        return Converters.toZadatakResponse(zadatakService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        zadatakService.deleteById(id);
    }

    @GetMapping("/{id}/projekat")
    public ResponseEntity<Resource> preuzmiProjekat(@PathVariable Long id){
        Resource fajl = zadatakService.preuzmiProjekat(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("zadatak-" + id + ".zip")
                                .build()
                                .toString())
                .body(fajl);
    }

    @GetMapping("/nastavnik/{nastavnikId}")
    public Page<ZadatakResponse> findByNastavnik(@PathVariable Long nastavnikId, Pageable pageable) {
        return zadatakService.findByNastavnik(nastavnikId, pageable).map(Converters::toZadatakResponse);
    }

    @GetMapping("/search")
    public Page<ZadatakResponse> search(@RequestParam String naziv, Pageable pageable) {
        return zadatakService.search(naziv, pageable).map(Converters::toZadatakResponse);
    }
}
