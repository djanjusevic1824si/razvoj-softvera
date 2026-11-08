package org.example.razvojsoftvera.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.razvojsoftvera.model.Predaja;
import org.example.razvojsoftvera.model.Rad;
import org.example.razvojsoftvera.model.StatusRada;
import org.example.razvojsoftvera.model.TipPredaje;
import org.example.razvojsoftvera.repositories.PredajaRepository;
import org.example.razvojsoftvera.repositories.RadRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class IstekPolaganjaService {

    private final RadRepository radRepository;
    private final PredajaRepository predajaRepository;

    @Value("${app.predaja.tolerancija}")
    private Duration tolerancija;

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void zatvoriIstekleRadove(){

        LocalDateTime granica = LocalDateTime.now().minus(tolerancija);
        List<Rad> radovi = radRepository.nadjiIstekleRadove(StatusRada.PREUZET, granica);
        for (Rad rad: radovi){
            Optional<Predaja> poslednjaPredaja = predajaRepository.findFirstByRad_IdOrderByRedniBrojDesc(rad.getId());

            if (poslednjaPredaja.isPresent()){
                poslednjaPredaja.get().setTipPredaje(TipPredaje.KONACNA);
            }
            else{
                Predaja prazna = new Predaja();
                prazna.setRad(rad);
                prazna.setRedniBroj(1);
                prazna.setTipPredaje(TipPredaje.KONACNA);
                prazna.setPutanjaResenja(rad.getGrupa().getZadatak().getPutanjaZadatka());
                predajaRepository.save(prazna);
            }

            rad.setStatusRada(StatusRada.PREDAT);
        }

        if (!radovi.isEmpty()){
            log.info("Automatski zatvoreno radova: {}", radovi.size());
        }
    }
}
