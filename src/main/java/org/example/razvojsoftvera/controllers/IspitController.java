package org.example.razvojsoftvera.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.requests.IspitCreateRequest;
import org.example.razvojsoftvera.requests.IspitUpdateRequest;
import org.example.razvojsoftvera.responses.IspitResponse;
import org.example.razvojsoftvera.services.IspitService;
import org.example.razvojsoftvera.utils.Converters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ispiti")
public class IspitController {

    private final IspitService ispitService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public IspitResponse save(
            @Valid @RequestBody IspitCreateRequest request
    ) {
        return Converters.toIspitResponse(ispitService.save(request));
    }

    @PutMapping("/{id}")
    public IspitResponse update(
            @PathVariable Long id,
            @Valid @RequestBody IspitUpdateRequest request
    ) {
        return Converters.toIspitResponse(ispitService.update(id, request));
    }

    @GetMapping("/all")
    public List<IspitResponse> findAll() {
        return Converters.toList(
                ispitService.findAll(),
                Converters::toIspitResponse
        );
    }

    @GetMapping("/svi")
    public Page<IspitResponse> findAll(Pageable pageable) {
        return ispitService.findAll(pageable)
                .map(Converters::toIspitResponse);
    }

    @GetMapping("/{id}")
    public IspitResponse findById(@PathVariable Long id) {
        return Converters.toIspitResponse(ispitService.findById(id));
    }

    @GetMapping("/rok/{ispitniRokId}")
    public Page<IspitResponse> findByIspitniRok(
            @PathVariable Long ispitniRokId,
            Pageable pageable
    ) {
        return ispitService.findByIspitniRok(ispitniRokId, pageable)
                .map(Converters::toIspitResponse);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        ispitService.deleteById(id);
    }
}