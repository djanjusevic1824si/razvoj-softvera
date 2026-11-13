package org.example.razvojsoftvera.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.requests.GrupaCreateRequest;
import org.example.razvojsoftvera.requests.GrupaUpdateRequest;
import org.example.razvojsoftvera.responses.GrupaResponse;
import org.example.razvojsoftvera.services.GrupaService;
import org.example.razvojsoftvera.utils.Converters;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/grupe")
public class GrupaController {

    private final GrupaService grupaService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public GrupaResponse add(@Valid @RequestBody GrupaCreateRequest request) {
        return Converters.toGrupaResponse(grupaService.save(request));
    }

    @GetMapping("/all")
    public List<GrupaResponse> findAll() {
        return Converters.toList(grupaService.findAll(), Converters::toGrupaResponse);
    }

    @GetMapping("/svi")
    public Page<GrupaResponse> findAll(@ParameterObject Pageable pageable) {
        return grupaService.findAll(pageable).map(Converters::toGrupaResponse);
    }

    @GetMapping("/{id}")
    public GrupaResponse findById(@PathVariable Long id) {
        return Converters.toGrupaResponse(grupaService.findById(id));
    }


    @PutMapping("/{id}")
    public GrupaResponse update(@PathVariable Long id, @Valid @RequestBody GrupaUpdateRequest request) {
        return Converters.toGrupaResponse(grupaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        grupaService.deleteById(id);
    }

    @GetMapping("/termin/{terminId}")
    public Page<GrupaResponse> findByTermin(@PathVariable Long terminId, @ParameterObject Pageable pageable) {
        return grupaService.findByTermin(terminId, pageable).map(Converters::toGrupaResponse);
    }
}
