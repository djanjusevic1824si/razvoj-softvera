package org.example.razvojsoftvera.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.requests.OcenaRequest;
import org.example.razvojsoftvera.responses.RadResponse;
import org.example.razvojsoftvera.responses.ResenjeResponse;
import org.example.razvojsoftvera.services.ResenjaService;
import org.example.razvojsoftvera.utils.Converters;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequiredArgsConstructor
public class ResenjeController {

    private final ResenjaService service;

    @GetMapping("/api/ispiti/{ispitId}/resenja")
    public List<ResenjeResponse> lista(@PathVariable Long ispitId,
                                       @RequestParam(required = false) Long grupaId) {
        return service.sve(ispitId, grupaId);
    }

    @GetMapping("/api/ispiti/{ispitId}/resenja/preuzmi")
    public ResponseEntity<StreamingResponseBody> preuzmi(@PathVariable Long ispitId,
                                                         @RequestParam(required = false) Long grupaId) {

        ResenjaService.SadrzajZipa sadrzaj = service.pripremiZip(ispitId, grupaId);

        StreamingResponseBody body = out -> {
            try (ZipOutputStream zip = new ZipOutputStream(out)) {
                for (ResenjaService.StavkaZipa s : sadrzaj.fajlovi()) {
                    zip.putNextEntry(new ZipEntry(s.imeUZipu()));
                    try (InputStream in = s.fajl().getInputStream()) {
                        in.transferTo(zip);
                    }
                    zip.closeEntry();
                }
                if (!sadrzaj.nedostaju().isEmpty()) {
                    zip.putNextEntry(new ZipEntry("NEDOSTAJU_FAJLOVI.txt"));
                    zip.write(String.join("\n", sadrzaj.nedostaju()).getBytes(StandardCharsets.UTF_8));
                    zip.closeEntry();
                }
            }
        };

        String ime = grupaId == null ? "resenja.zip" : "resenja_grupa_" + grupaId + ".zip";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + ime + "\"")
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(body);
    }

    @GetMapping("/api/ispiti/{ispitId}/moje-resenje")
    public RadResponse mojeResenje(@PathVariable Long ispitId) {
        return Converters.toRadResponse(service.mojeResenje(ispitId));
    }

    @PutMapping("/api/predaje/{predajaId}/ocena")
    public RadResponse oceni(@PathVariable Long predajaId, @Valid @RequestBody OcenaRequest z) {
        return Converters.toRadResponse(service.oceni(predajaId, z));
    }
}
