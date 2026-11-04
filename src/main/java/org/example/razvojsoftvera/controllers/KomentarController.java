package org.example.razvojsoftvera.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.requests.*;
import org.example.razvojsoftvera.responses.KomentarResponse;
import org.example.razvojsoftvera.services.KomentarService;
import org.example.razvojsoftvera.utils.Converters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/komentari")
public class KomentarController {

    private final KomentarService komentarService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public KomentarResponse save(
            @Valid @RequestBody KomentarCreateRequest request
    ) {
        return Converters.toKomentarResponse(
                komentarService.save(request)
        );
    }

    @PutMapping("/{id}")
    public KomentarResponse update(
            @PathVariable Long id,
            @Valid @RequestBody KomentarUpdateRequest request
    ) {
        return Converters.toKomentarResponse(
                komentarService.update(id, request)
        );
    }

    @GetMapping("/all")
    public List<KomentarResponse> findAll() {
        return Converters.toList(
                komentarService.findAll(),
                Converters::toKomentarResponse
        );
    }

    @GetMapping("/svi")
    public Page<KomentarResponse> findAll(Pageable pageable) {
        return komentarService.findAll(pageable)
                .map(Converters::toKomentarResponse);
    }

    @GetMapping("/{id}")
    public KomentarResponse findById(@PathVariable Long id) {
        return Converters.toKomentarResponse(
                komentarService.findById(id)
        );
    }

    @GetMapping("/rad/{radId}")
    public Page<KomentarResponse> findByRad(
            @PathVariable Long radId,
            Pageable pageable
    ) {
        return komentarService.findByRad(radId, pageable)
                .map(Converters::toKomentarResponse);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        komentarService.deleteById(id);
    }
}