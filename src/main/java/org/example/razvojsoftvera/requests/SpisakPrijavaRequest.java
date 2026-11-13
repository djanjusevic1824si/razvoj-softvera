package org.example.razvojsoftvera.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;


import java.util.List;

public record SpisakPrijavaRequest(
        @NotEmpty List<@Valid StavkaSpiska> spisakIndeksa
) { }
