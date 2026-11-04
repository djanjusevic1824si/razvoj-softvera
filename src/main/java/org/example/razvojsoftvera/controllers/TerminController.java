package org.example.razvojsoftvera.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.requests.TerminCreateRequest;
import org.example.razvojsoftvera.requests.TerminUpdateRequest;
import org.example.razvojsoftvera.responses.TerminResponse;
import org.example.razvojsoftvera.services.TerminService;
import org.example.razvojsoftvera.utils.Converters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/termini")
public class TerminController {

    private final TerminService terminService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public TerminResponse add(@Valid @RequestBody TerminCreateRequest request) {
        return Converters.toTerminResponse(terminService.save(request));
    }

    @GetMapping("/all")
    public List<TerminResponse> findAll() {
        return Converters.toList(terminService.findAll(), Converters::toTerminResponse);
    }

    @GetMapping("/svi")
    public Page<TerminResponse> findAll(Pageable pageable) {
        return terminService.findAll(pageable).map(Converters::toTerminResponse);
    }

    @GetMapping("/{id}")
    public TerminResponse findById(@PathVariable Long id) {
        return Converters.toTerminResponse(terminService.findById(id));
    }

    @PutMapping("/{id}")
    public TerminResponse update(@PathVariable Long id, @Valid @RequestBody TerminUpdateRequest request) {
        return Converters.toTerminResponse(terminService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        terminService.deleteById(id);
    }

    @GetMapping("/ispit/{ispitId}")
    public Page<TerminResponse> findByIspit(@PathVariable Long ispitId, Pageable pageable) {
        return terminService.findByIspit(ispitId, pageable).map(Converters::toTerminResponse);
    }
}
