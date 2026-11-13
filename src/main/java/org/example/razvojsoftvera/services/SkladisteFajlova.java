package org.example.razvojsoftvera.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface SkladisteFajlova {
    String sacuvaj(MultipartFile fajl, String folder);

    Resource ucitaj(String putanja);
}
