package org.example.razvojsoftvera.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record ZadatakCreateRequest(
        @NotBlank String naziv,
        String opis,
        @NotNull Long nastavnikId,
        @NotNull @Schema(type = "string", format = "binary") MultipartFile fajl
) {}
