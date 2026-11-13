package org.example.razvojsoftvera.responses;

import java.util.List;

public record SpisakPrijavaResponse(
        int dodato,
        List<String> greske
) {
}
