package org.example.razvojsoftvera.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.example.razvojsoftvera.model.TipPredaje;
import org.springframework.web.multipart.MultipartFile;

public record PredajaCreateRequest(
        @NotNull TipPredaje tipPredaje,
        @NotNull @Schema(type = "string", format = "binary") MultipartFile fajl
) {}