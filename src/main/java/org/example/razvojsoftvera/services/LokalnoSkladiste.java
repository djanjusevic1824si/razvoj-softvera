package org.example.razvojsoftvera.services;

import org.springframework.core.io.Resource;
import org.example.razvojsoftvera.exceptions.BadRequestException;
import org.example.razvojsoftvera.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LokalnoSkladiste implements SkladisteFajlova{

    private final Path root;

    public LokalnoSkladiste(@Value("${app.storage.root}") String root) {
        this.root = Paths.get(root).toAbsolutePath().normalize();
    }

    @Override
    public String sacuvaj(MultipartFile fajl, String folder) {
        if (fajl.isEmpty() || fajl == null){
            throw new BadRequestException("Fajl nije poslat ili je prazan");
        }

        String originalnoIme = fajl.getOriginalFilename();
        if (originalnoIme == null || !originalnoIme.toLowerCase().endsWith(".zip")){
            throw new BadRequestException("Dozvoljen je samo .zip tim fajla");
        }

        Path cilj = root.resolve(folder)
                .resolve(UUID.randomUUID() + ".zip")
                .normalize();

        if (!cilj.startsWith(root)){
            throw new BadRequestException("Neispravna putanja fajla");
        }

        try (InputStream ulaz = fajl.getInputStream()){
            Files.createDirectories(cilj.getParent());
            Files.copy(ulaz, cilj, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e){
            throw new UncheckedIOException("Fajl nije moguce sacuvati", e);
        }

        return root.relativize(cilj).toString().replace('\\', '/');
    }

    @Override
    public Resource ucitaj(String putanja) {
        Path fajl = root.resolve(putanja).normalize();

        if (!fajl.startsWith(root) || !Files.isRegularFile(fajl)){
            throw new ResourceNotFoundException("Fajl ne postoji");
        }

        return new FileSystemResource(fajl);
    }
}
