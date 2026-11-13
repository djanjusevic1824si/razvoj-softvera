package org.example.razvojsoftvera.utils;

import org.example.razvojsoftvera.model.*;
import org.example.razvojsoftvera.responses.*;

import java.util.List;
import java.util.function.Function;

public final class Converters {

    private Converters() {
    }

    public static KorisnikResponse toKorisnikResponse(Korisnik korisnik) {
        return new KorisnikResponse(
                korisnik.getId(), korisnik.getEmail(), korisnik.getUloga()
        );
    }

    public static NastavnikResponse toNastavnikResponse(Nastavnik nastavnik) {
        return new NastavnikResponse(
                nastavnik.getId(),
                nastavnik.getIme(),
                nastavnik.getPrezime(),
                nastavnik.getZvanje(),
                nastavnik.getKorisnik().getId()
        );
    }

    public static StudentResponse toStudentResponse(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getIme(),
                student.getPrezime(),
                student.getJmbg(),
                student.getKorisnik() == null ? null : student.getKorisnik().getId()
        );
    }

    public static <T, R> List<R> toList(List<T> list, Function<T, R> converter) {
        return list.stream().map(converter).toList();
    }

    public static StudijskiProgramResponse toStudijskiProgramResponse(StudijskiProgram studijskiProgram) {
        return new StudijskiProgramResponse(
                studijskiProgram.getId(),
                studijskiProgram.getOznaka(),
                studijskiProgram.getNaziv()
        );
    }

    public static PredmetResponse toPredmetResponse(Predmet predmet){
        return new PredmetResponse(
                predmet.getId(),
                predmet.getNaziv(),
                predmet.getSifra()
        );
    }

    public static IspitniRokResponse toIspitniRokResponse(IspitniRok ispitniRok){
        return new IspitniRokResponse(
                ispitniRok.getId(),
                ispitniRok.getNaziv(),
                ispitniRok.getSkolskaGodina(),
                ispitniRok.getDatumOd(),
                ispitniRok.getDatumDo()
        );
    }

    public static TerminResponse toTerminResponse(Termin termin){
        return new TerminResponse(
                termin.getId(),
                termin.getPocetakIzrade(),
                termin.getKrajIzrade(),
                toIspitResponse(termin.getIspit())
        );
    }

    public static GrupaResponse toGrupaResponse(Grupa grupa) {
        return new GrupaResponse(
                grupa.getId(),
                grupa.getNaziv(),
                grupa.getTermin().getId(),
                grupa.getZadatak().getId(),
                grupa.getZadatak().getNaziv()
        );
    }

    public static ZadatakResponse toZadatakResponse(Zadatak zadatak) {
        return new ZadatakResponse(
                zadatak.getId(),
                zadatak.getNaziv(),
                zadatak.getOpis(),
                zadatak.getDatumObjavljivanja(),
                zadatak.getNastavnik().getId(),
                zadatak.getNastavnik().getIme(),
                zadatak.getNastavnik().getPrezime()
        );
    }

    public static PrijavaResponse toPrijavaResponse(Prijava prijava) {
        StudentIndeks indeks = prijava.getStudentIndeks();

        return new PrijavaResponse(
                prijava.getId(),
                prijava.getIspit().getId(),
                indeks.getId(),
                indeks.getStudent().getId(),
                indeks.getStudent().getIme(),
                indeks.getStudent().getPrezime(),
                indeks.getBroj(),
                indeks.getGodinaUpisa(),
                indeks.getStudijskiProgram().getId()
        );
    }

    public static IspitResponse toIspitResponse(Ispit ispit) {
        return new IspitResponse(
                ispit.getId(),
                ispit.getPredmet().getId(),
                ispit.getIspitniRok().getId()
        );
    }

    public static DrziPredmetResponse toDrziPredmetResponse(
            DrziPredmet drziPredmet
    ) {
        return new DrziPredmetResponse(
                drziPredmet.getId(),
                drziPredmet.getNastavnik().getId(),
                drziPredmet.getPredmet().getId(),
                drziPredmet.getStudijskiProgram().getId()
        );
    }

    public static StudentIndeksResponse toStudentIndeksResponse(
            StudentIndeks indeks
    ) {
        return new StudentIndeksResponse(
                indeks.getId(),
                indeks.getBroj(),
                indeks.getGodinaUpisa(),
                indeks.isAktivan(),
                indeks.getStudent().getId(),
                indeks.getStudijskiProgram().getId()
        );
    }

    public static RadResponse toRadResponse(Rad rad) {
        return new RadResponse(
                rad.getId(),
                rad.getPrijava().getId(),
                rad.getGrupa().getId(),
                rad.getUcionica(),
                rad.getStatusRada(),
                rad.getVremePreuzimanja(),
                rad.getPoeni(),
                rad.getOcenioNastavnik() == null
                        ? null
                        : rad.getOcenioNastavnik().getId(),
                rad.getVremeOcenjivanja()
        );
    }

    public static PredajaResponse toPredajaResponse(Predaja predaja) {
        return new PredajaResponse(
                predaja.getId(),
                predaja.getRad().getId(),
                predaja.getRedniBroj(),
                predaja.getTipPredaje(),
                predaja.getVreme(),
                predaja.getPutanjaResenja()
        );
    }

    public static KomentarResponse toKomentarResponse(Komentar komentar) {
        return new KomentarResponse(
                komentar.getId(),
                komentar.getRad().getId(),
                komentar.getNastavnik().getId(),
                komentar.getPutanjaFajla(),
                komentar.getLinijaOd(),
                komentar.getLinijaDo(),
                komentar.getTekst(),
                komentar.getVremePostavljanja()
        );
    }

    public static StudentListResponse toStudentListResponse(Student student) {
        return new StudentListResponse(
                student.getId(),
                student.getIme(),
                student.getPrezime()
        );
    }

    public static PolaganjeResponse toPolaganjeResponse(Rad rad){
        Grupa grupa = rad.getGrupa();
        Termin termin = grupa.getTermin();

        return new PolaganjeResponse(
                rad.getId(),
                rad.getStatusRada(),
                rad.getVremePreuzimanja(),
                rad.getUcionica(),
                grupa.getId(),
                grupa.getNaziv(),
                termin.getId(),
                termin.getPocetakIzrade(),
                termin.getKrajIzrade(),
                termin.getIspit().getPredmet().getNaziv(),
                grupa.getZadatak().getNaziv()
        );
    }
}
