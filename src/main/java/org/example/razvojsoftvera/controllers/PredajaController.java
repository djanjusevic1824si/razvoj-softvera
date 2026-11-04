package org.example.razvojsoftvera.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.requests.*;
import org.example.razvojsoftvera.responses.PredajaResponse;
import org.example.razvojsoftvera.services.PredajaService;
import org.example.razvojsoftvera.utils.Converters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/predaje")
public class PredajaController {

    private final PredajaService predajaService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public PredajaResponse save(
            @Valid @RequestBody PredajaCreateRequest request
    ) {
        return Converters.toPredajaResponse(predajaService.save(request));
    }

    @PutMapping("/{id}")
    public PredajaResponse update(
            @PathVariable Long id,
            @Valid @RequestBody PredajaUpdateRequest request
    ) {
        return Converters.toPredajaResponse(
                predajaService.update(id, request)
        );
    }

    @GetMapping("/all")
    public List<PredajaResponse> findAll() {
        return Converters.toList(
                predajaService.findAll(),
                Converters::toPredajaResponse
        );
    }

    @GetMapping("/svi")
    public Page<PredajaResponse> findAll(Pageable pageable) {
        return predajaService.findAll(pageable)
                .map(Converters::toPredajaResponse);
    }

    @GetMapping("/{id}")
    public PredajaResponse findById(@PathVariable Long id) {
        return Converters.toPredajaResponse(predajaService.findById(id));
    }

    @GetMapping("/rad/{radId}")
    public Page<PredajaResponse> findByRad(
            @PathVariable Long radId,
            Pageable pageable
    ) {
        return predajaService.findByRad(radId, pageable)
                .map(Converters::toPredajaResponse);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        predajaService.deleteById(id);
    }
}