package org.example.razvojsoftvera.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.requests.PolaganjeStartRequest;
import org.example.razvojsoftvera.requests.PredajaCreateRequest;
import org.example.razvojsoftvera.responses.PolaganjeResponse;
import org.example.razvojsoftvera.responses.PredajaResponse;
import org.example.razvojsoftvera.services.PolaganjeService;
import org.example.razvojsoftvera.utils.Converters;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/polaganje")
public class PolaganjeController {

    private final PolaganjeService polaganjeService;

    @PostMapping("/start")
    public PolaganjeResponse polaganjeResponse(@Valid @RequestBody PolaganjeStartRequest request){
        return Converters.toPolaganjeResponse(polaganjeService.start(request));
    }

    @GetMapping("/{radId}/projekat")
    public ResponseEntity<Resource> preuzmiProjekat(@PathVariable Long radId){
        Resource fajl = polaganjeService.preuzmiProjekat(radId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                        .filename("projekat.zip")
                        .build()
                        .toString())
        .body(fajl);
    }

    @PostMapping(value = "/{radId}/predaja", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public PredajaResponse predaj(
            @PathVariable Long radId,
            @Valid @ModelAttribute PredajaCreateRequest request
            ){
        return Converters.toPredajaResponse(polaganjeService.predaja(radId, request));
    }
}
