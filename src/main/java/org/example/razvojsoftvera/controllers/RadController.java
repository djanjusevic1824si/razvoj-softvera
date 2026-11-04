package org.example.razvojsoftvera.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.requests.*;
import org.example.razvojsoftvera.responses.RadResponse;
import org.example.razvojsoftvera.services.RadService;
import org.example.razvojsoftvera.utils.Converters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/radovi")
public class RadController {

    private final RadService radService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public RadResponse save(
            @Valid @RequestBody RadCreateRequest request
    ) {
        return Converters.toRadResponse(radService.save(request));
    }

    @PutMapping("/{id}")
    public RadResponse update(
            @PathVariable Long id,
            @Valid @RequestBody RadUpdateRequest request
    ) {
        return Converters.toRadResponse(radService.update(id, request));
    }

    @PutMapping("/{id}/ocena")
    public RadResponse oceni(
            @PathVariable Long id,
            @Valid @RequestBody RadOcenaRequest request
    ) {
        return Converters.toRadResponse(radService.oceni(id, request));
    }

    @GetMapping("/all")
    public List<RadResponse> findAll() {
        return Converters.toList(
                radService.findAll(),
                Converters::toRadResponse
        );
    }

    @GetMapping("/svi")
    public Page<RadResponse> findAll(Pageable pageable) {
        return radService.findAll(pageable)
                .map(Converters::toRadResponse);
    }

    @GetMapping("/{id}")
    public RadResponse findById(@PathVariable Long id) {
        return Converters.toRadResponse(radService.findById(id));
    }

    @GetMapping("/ispit/{ispitId}")
    public Page<RadResponse> findByIspit(
            @PathVariable Long ispitId,
            Pageable pageable
    ) {
        return radService.findByIspit(ispitId, pageable)
                .map(Converters::toRadResponse);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        radService.deleteById(id);
    }
}