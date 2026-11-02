package org.example.razvojsoftvera.utils;

import org.example.razvojsoftvera.requests.NastavnikRequest;
import org.example.razvojsoftvera.requests.StudentRequest;
import org.example.razvojsoftvera.responses.KorisnikResponse;
import org.example.razvojsoftvera.responses.NastavnikResponse;
import org.example.razvojsoftvera.responses.StudentResponse;
import org.example.razvojsoftvera.model.Korisnik;
import org.example.razvojsoftvera.model.Nastavnik;
import org.example.razvojsoftvera.model.Student;

import java.util.List;
import java.util.function.Function;

public final class Converters {

    private Converters() {
    }

    public static Nastavnik toNastavnik(NastavnikRequest request) {
        Nastavnik nastavnik = new Nastavnik();
        nastavnik.setIme(request.ime().strip());
        nastavnik.setPrezime(request.prezime().strip());
        nastavnik.setZvanje(request.zvanje());
        return nastavnik;
    }

    public static Student toStudent(StudentRequest request) {
        Student student = new Student();
        student.setIme(request.ime().strip());
        student.setPrezime(request.prezime().strip());
        student.setJmbg(request.jmbg() == null || request.jmbg().isBlank()
                ? null : request.jmbg().strip());
        return student;
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
}
