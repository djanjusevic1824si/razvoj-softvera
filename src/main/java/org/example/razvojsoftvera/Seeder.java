package org.example.razvojsoftvera;

import lombok.RequiredArgsConstructor;
import org.example.razvojsoftvera.model.*;
import org.example.razvojsoftvera.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class Seeder implements CommandLineRunner {

    private final KorisnikRepository korisnikRepository;
    private final StudentRepository studentRepository;
    private final NastavnikRepository nastavnikRepository;
    private final StudijskiProgramRepository studijskiProgramRepository;
    private final StudentIndeksRepository studentIndeksRepository;
    private final PredmetRepository predmetRepository;
    private final DrziPredmetRepository drziPredmetRepository;
    private final IspitniRokRepository ispitniRokRepository;
    private final IspitRepository ispitRepository;
    private final TerminRepository terminRepository;
    private final ZadatakRepository zadatakRepository;
    private final GrupaRepository grupaRepository;
    private final PrijavaRepository prijavaRepository;
    private final RadRepository radRepository;
    private final PredajaRepository predajaRepository;
    private final KomentarRepository komentarRepository;

    private final PasswordEncoder passwordEncoder;
    private final Environment env;

    @Override
    @Transactional
    public void run(String... args) {

        if (korisnikRepository.count() > 0
                || studentRepository.count() > 0
                || nastavnikRepository.count() > 0
                || studijskiProgramRepository.count() > 0
                || studentIndeksRepository.count() > 0
                || predmetRepository.count() > 0
                || drziPredmetRepository.count() > 0
                || ispitniRokRepository.count() > 0
                || ispitRepository.count() > 0
                || terminRepository.count() > 0
                || zadatakRepository.count() > 0
                || grupaRepository.count() > 0
                || prijavaRepository.count() > 0
                || radRepository.count() > 0
                || predajaRepository.count() > 0
                || komentarRepository.count() > 0) {
            return;
        }

        String lozinka = env.getProperty(
                "app.seed.lozinka",
                "lozinka123"
        );

        String[] imenaStudenata = {
                "Marko", "Ana", "Nikola", "Milica", "Luka",
                "Jovana", "Stefan", "Teodora", "Petar", "Sara"
        };

        String[] imenaNastavnika = {
                "Milan", "Jelena", "Dragan", "Marija", "Ivan",
                "Ivana", "Aleksandar", "Sanja", "Vladimir", "Tamara"
        };

        String[] prezimena = {
                "Petrovic", "Jovanovic", "Nikolic", "Ilic", "Markovic",
                "Stojkovic", "Pavlovic", "Popovic", "Djordjevic", "Simic"
        };

        String[] naziviPredmeta = {
                "Uvod u programiranje",
                "Objektno orijentisano programiranje",
                "Algoritmi i strukture podataka",
                "Baze podataka",
                "Razvoj softvera",
                "Veb programiranje",
                "Operativni sistemi",
                "Racunarske mreze",
                "Testiranje softvera",
                "Distribuirani sistemi"
        };

        LocalDateTime pocetak = LocalDateTime.now().minusMinutes(15);

        int godina = pocetak.getYear();
        String skolskaGodina = pocetak.getMonthValue() >= 10
                ? godina + "/" + (godina + 1)
                : (godina - 1) + "/" + godina;

        for (int i = 1; i <= 10; i++) {

            StudijskiProgram program = new StudijskiProgram();
            program.setOznaka("SP" + i);
            program.setNaziv("Studijski program " + i);
            program = studijskiProgramRepository.save(program);

            Predmet predmet = new Predmet();
            predmet.setSifra("PR" + i);
            predmet.setNaziv(naziviPredmeta[i - 1]);
            predmet = predmetRepository.save(predmet);

            Korisnik korisnikNastavnik = new Korisnik();
            korisnikNastavnik.setEmail(
                    "nastavnik" + i + "@example.com"
            );
            korisnikNastavnik.setLozinka(
                    passwordEncoder.encode(lozinka)
            );
            korisnikNastavnik.setUloga(Uloga.NASTAVNIK);
            korisnikNastavnik = korisnikRepository.save(
                    korisnikNastavnik
            );

            Nastavnik nastavnik = new Nastavnik();
            nastavnik.setIme(imenaNastavnika[i - 1]);
            nastavnik.setPrezime(prezimena[i - 1]);
            nastavnik.setZvanje(
                    i % 2 == 0 ? "Docent" : "Vanredni profesor"
            );
            nastavnik.setKorisnik(korisnikNastavnik);
            nastavnik = nastavnikRepository.save(nastavnik);

            Korisnik korisnikStudent = new Korisnik();
            korisnikStudent.setEmail(
                    "student" + i + "@example.com"
            );
            korisnikStudent.setLozinka(
                    passwordEncoder.encode(lozinka)
            );
            korisnikStudent.setUloga(Uloga.STUDENT);
            korisnikStudent = korisnikRepository.save(
                    korisnikStudent
            );

            Student student = new Student();
            student.setIme(imenaStudenata[i - 1]);
            student.setPrezime(prezimena[i - 1]);

            student.setJmbg("0000000" + String.format("%06d", i));

            student.setKorisnik(korisnikStudent);
            student = studentRepository.save(student);

            StudentIndeks indeks = new StudentIndeks();
            indeks.setBroj(i);
            indeks.setGodinaUpisa(godina - 1);
            indeks.setAktivan(true);
            indeks.setStudent(student);
            indeks.setStudijskiProgram(program);
            indeks = studentIndeksRepository.save(indeks);

            DrziPredmet drziPredmet = new DrziPredmet();
            drziPredmet.setNastavnik(nastavnik);
            drziPredmet.setPredmet(predmet);
            drziPredmet.setStudijskiProgram(program);
            drziPredmetRepository.save(drziPredmet);

            IspitniRok rok = new IspitniRok();
            rok.setNaziv("Probni rok " + i);
            rok.setSkolskaGodina(skolskaGodina);
            rok.setDatumOd(pocetak.toLocalDate());
            rok.setDatumDo(pocetak.toLocalDate().plusDays(7));
            rok = ispitniRokRepository.save(rok);

            Ispit ispit = new Ispit();
            ispit.setPredmet(predmet);
            ispit.setIspitniRok(rok);
            ispit = ispitRepository.save(ispit);

            Termin termin = new Termin();
            termin.setPocetakIzrade(pocetak);
            termin.setKrajIzrade(pocetak.plusHours(2));
            termin.setIspit(ispit);
            termin = terminRepository.save(termin);

            Zadatak zadatak = new Zadatak();
            zadatak.setNaziv("Zadatak " + i);
            zadatak.setOpis(
                    "Implementirati projekat za predmet "
                            + predmet.getNaziv() + "."
            );

            zadatak.setPutanjaZadatka(
                    "zadaci/projekat-" + i + ".zip"
            );

            zadatak.setNastavnik(nastavnik);
            zadatak = zadatakRepository.save(zadatak);

            Grupa grupa = new Grupa();
            grupa.setNaziv(i % 2 == 0 ? "B" : "A");
            grupa.setTermin(termin);
            grupa.setZadatak(zadatak);
            grupa = grupaRepository.save(grupa);

            Prijava prijava = new Prijava();
            prijava.setStudentIndeks(indeks);
            prijava.setIspit(ispit);
            prijava = prijavaRepository.save(prijava);

            boolean predat = i > 5;

            Rad rad = new Rad();
            rad.setUcionica("Ucionica " + i);
            rad.setStatusRada(
                    predat ? StatusRada.PREDAT : StatusRada.PREUZET
            );
            rad.setPrijava(prijava);
            rad.setGrupa(grupa);
            rad = radRepository.save(rad);

            Predaja predaja = new Predaja();
            predaja.setRedniBroj(1);
            predaja.setTipPredaje(
                    predat ? TipPredaje.KONACNA : TipPredaje.AUTOMATSKA
            );
            predaja.setPutanjaResenja(
                    "resenja/student-" + i + "/predaja-1.zip"
            );
            predaja.setRad(rad);
            predajaRepository.save(predaja);

            if (i >= 8) {
                radRepository.flush();

                rad.setPoeni(
                        BigDecimal.valueOf(70 + i).setScale(1)
                );
                rad.setOcenioNastavnik(nastavnik);
                rad.setVremeOcenjivanja(LocalDateTime.now());
                radRepository.save(rad);
            }

            if (predat) {
                for (int j = 1; j <= 2; j++) {
                    Komentar komentar = new Komentar();
                    komentar.setPutanjaFajla(
                            "src/main/java/Main.java"
                    );
                    komentar.setLinijaOd(j == 1 ? 1 : 10);
                    komentar.setLinijaDo(j == 1 ? 5 : 15);
                    komentar.setTekst(
                            j == 1
                                    ? "Dobra struktura resenja."
                                    : "Dodati proveru ulaznih podataka."
                    );
                    komentar.setRad(rad);
                    komentar.setNastavnik(nastavnik);
                    komentarRepository.save(komentar);
                }
            }
        }
    }
}