package org.example.razvojsoftvera.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.requests.DrziPredmetCreateRequest;
import org.example.razvojsoftvera.requests.DrziPredmetUpdateRequest;
import org.example.razvojsoftvera.responses.DrziPredmetResponse;
import org.example.razvojsoftvera.services.DrziPredmetService;
import org.example.razvojsoftvera.utils.Converters;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/drzi-predmet")
public class DrziPredmetController {

    private final DrziPredmetService drziPredmetService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public DrziPredmetResponse save(
            @Valid @RequestBody DrziPredmetCreateRequest request
    ) {
        return Converters.toDrziPredmetResponse(
                drziPredmetService.save(request)
        );
    }

    @PutMapping("/{id}")
    public DrziPredmetResponse update(
            @PathVariable Long id,
            @Valid @RequestBody DrziPredmetUpdateRequest request
    ) {
        return Converters.toDrziPredmetResponse(
                drziPredmetService.update(id, request)
        );
    }

    @GetMapping("/all")
    public List<DrziPredmetResponse> findAll() {
        return Converters.toList(
                drziPredmetService.findAll(),
                Converters::toDrziPredmetResponse
        );
    }

    @GetMapping("/svi")
    public Page<DrziPredmetResponse> findAll(
            @ParameterObject Pageable pageable) {
        return drziPredmetService.findAll(pageable)
                .map(Converters::toDrziPredmetResponse);
    }

    @GetMapping("/{id}")
    public DrziPredmetResponse findById(@PathVariable Long id) {
        return Converters.toDrziPredmetResponse(
                drziPredmetService.findById(id)
        );
    }

    @GetMapping("/nastavnik/{nastavnikId}")
    public Page<DrziPredmetResponse> findByNastavnik(
            @PathVariable Long nastavnikId,
            Pageable pageable
    ) {
        return drziPredmetService.findByNastavnik(nastavnikId, pageable)
                .map(Converters::toDrziPredmetResponse);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        drziPredmetService.deleteById(id);
    }
}